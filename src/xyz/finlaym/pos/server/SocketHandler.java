package xyz.finlaym.pos.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jdk.net.ExtendedSocketOptions;
import jdk.net.UnixDomainPrincipal;
import xyz.finlaym.pos.data.Comment;
import xyz.finlaym.pos.data.Customer;
import xyz.finlaym.pos.data.Order;
import xyz.finlaym.pos.data.OrderLine;
import xyz.finlaym.pos.data.Payment;
import xyz.finlaym.pos.data.Product;
import xyz.finlaym.pos.data.Return;
import xyz.finlaym.pos.data.User;
import xyz.finlaym.pos.server.connector.DataConnection;

public class SocketHandler extends Thread{
	private DataConnection connector;
	private SocketChannel channel;
	public SocketHandler(DataConnection connector, SocketChannel channel) {
		this.connector = connector;
		this.channel = channel;
		start();
	}
	@Override
	public void run() {
		try {
			UnixDomainPrincipal pr = channel.getOption(ExtendedSocketOptions.SO_PEERCRED);
			User user = connector.getUser(pr.user().getName());
			if(user == null) {
				System.err.println("Error: User not found!");
				channel.close();
				return;
			}
			
			while(channel.isOpen()) {
				String data = read();
				JSONObject object = new JSONObject(data);
				String cmd = object.getString("command");
				switch(cmd.toLowerCase()) {
				case "create_order":
					JSONObject result = new JSONObject();
					JSONObject orderJSON = object.getJSONObject("order");
					int type = orderJSON.getInt("type");
					Customer customer = connector.getCustomer(orderJSON.getInt("customer"));
					if(customer == null) {
						result.put("status", "customer_not_found");
						write(result.toString());
						break;
					}
					List<OrderLine> lines = new ArrayList<OrderLine>();
					int id = connector.getNextOrderLineId();
					int subtotal = 0;
					boolean invalid = false;
					
					for(Object obj : orderJSON.getJSONArray("lines")) {
						JSONObject o = (JSONObject) obj;
						int product = o.getInt("product");
						int count = o.getInt("count");
						int origPrice = o.getInt("originalPrice");
						int price = o.getInt("price");
						subtotal += price * count;
						int overrideReason = o.getInt("overrideReason");
						Product p = connector.getProduct(product);
						if(p == null) {
							result.put("status", "product_not_found");
							write(result.toString());
							invalid = true;
							break;
						}
						OrderLine line = new OrderLine(p, count, origPrice, price, overrideReason, id);
						boolean ret = count < 0;
						if(ret) {
							int origOrder = o.getInt("originalOrder");
							OrderLine orig = connector.getOrderLine(origOrder);
							if(orig == null) {
								result.put("status", "return_not_found");
								write(result.toString());
								invalid = true;
								break;
							}
							if(orig.getProduct().getId() != p.getId() || (orig.getCount()-orig.getCountReturned()) < -count) {
								result.put("status", "invalid_return_line");
								write(result.toString());
								invalid = true;
								break;
							}
							if(orig.getPrice() != line.getPrice()) {
								result.put("status", "invalid_return_price");
								write(result.toString());
								invalid = true;
								break;
							}
							orig.setCountReturned(orig.getCountReturned()-count);
							Return r = new Return(line, orig);
							line.setRet(r);
						}
						lines.add(line);
						id++;
					}
					if(invalid)
						break;
					int total = (int) (subtotal * 1.13);
					if(customer.isTaxExempt()) {
						total = (int) (subtotal * 1.08);
					}
					List<Comment> comments = new ArrayList<Comment>();
					id = connector.getNextOrderCommentId();
					for(Object obj : orderJSON.getJSONArray("comments")) {
						JSONObject o = (JSONObject) obj;
						int cType = o.getInt("type");
						String value = o.getString("value");
						Comment comment = new Comment(id, value, cType, user);
						comments.add(comment);
						id++;
					}
					List<Payment> payments = new ArrayList<Payment>();
					id = connector.getNextPaymentId();
					int oid = connector.getNextOrderId();
					for(Object obj : orderJSON.getJSONArray("payments")) {
						JSONObject o = (JSONObject) obj;
						int pType = o.getInt("type");
						int amount = o.getInt("amount");
						String ident = o.getString("identifier");
						String auth = o.getString("authorization");
						Payment payment = new Payment(id, user, pType, amount, ident, auth, oid);
						payments.add(payment);
						id++;
					}
					int status = 0;
					Order order = new Order(oid, lines, subtotal, total, customer, comments, user, payments, type, status,0);
					connector.createOrder(order);
					result.put("status", "success");
					result.put("order", order.getId());
					write(result.toString());
					break;
				case "get_order":
					id = object.getInt("id");
					order = connector.getOrder(id);
					if(order == null) {
						result = new JSONObject();
						result.put("error", "not_found");
					}else {
						result = new JSONObject();
						result.put("status", "success");
						result.put("order", order.toJSON());
					}
					write(result.toString());
					break;
				case "get_customer":
					id = object.getInt("id");
					customer = connector.getCustomer(id);
					result = new JSONObject();
					if(customer == null) {
						result.put("status", "not_found");
					}else {
						result.put("status", "success");
						result.put("customer", customer.toJSON());
					}
					write(result.toString());
					break;
				case "get_product":
					id = object.getInt("id");
					Product product = connector.getProduct(id);
					result = new JSONObject();
					if(product == null) {
						result.put("status", "not_found");
					}else {
						result.put("status", "success");
						result.put("product", product.toJSON());
					}
					write(result.toString());
					break;
				case "find_payment":
					String identifier = object.getString("identifier");
					List<Payment> paymentList = connector.findPayment(identifier);
					result = new JSONObject();
					
					JSONArray arr = new JSONArray();
					for(Payment p : paymentList) {
						arr.put(p.toJSON());
					}
					result.put("status", "success");
					result.put("payments", arr);
					write(result.toString());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				write("{\"status\": \"error\"}");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	private String read() throws Exception{
		StringBuffer str = new StringBuffer();
		int count = 0;
		ByteBuffer data = ByteBuffer.allocate(10000);
		while(count < 5000) {
			int numRead = channel.read(data);
			if(numRead > 0) {
				String c = new String(data.array());
				str.append(c);
				if(c.contains("\n")) {
					return str.toString().strip();
				}
				count++;
			}
		}
		return null;
	}
	private void write(String s) throws Exception{
		s += "\n"; // Append new line to trigger read
		byte[] encoded = s.getBytes();
		channel.write(ByteBuffer.wrap(encoded));
	}
}
