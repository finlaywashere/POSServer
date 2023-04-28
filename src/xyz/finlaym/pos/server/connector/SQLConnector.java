package xyz.finlaym.pos.server.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import xyz.finlaym.pos.data.Comment;
import xyz.finlaym.pos.data.Customer;
import xyz.finlaym.pos.data.Order;
import xyz.finlaym.pos.data.OrderLine;
import xyz.finlaym.pos.data.Payment;
import xyz.finlaym.pos.data.Product;
import xyz.finlaym.pos.data.Return;
import xyz.finlaym.pos.data.User;;

public class SQLConnector extends DataConnection{
	private Connection connector;
	
	public SQLConnector() throws Exception{
		String host = System.getenv("host");
		String db = System.getenv("database");
		String user = System.getenv("username");
		String pass = System.getenv("password");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connector = DriverManager.getConnection("jdbc:mysql://"+host+"/"+db+"?user="+user+"&password="+pass);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void createOrder(Order order) throws Exception{
		PreparedStatement s = connector.prepareStatement("INSERT INTO orders (id, type, status, subtotal, total, user) VALUES (?,?,?,?,?,?);");
		s.setInt(1, order.getId());
		s.setInt(2, order.getType());
		s.setInt(3, order.getStatus());
		s.setInt(4, order.getSubtotal());
		s.setInt(5, order.getTotal());
		s.setInt(6, order.getCreator().getId());
		s.execute();
		for(OrderLine l : order.getLines()) {
			createOrderLine(order, l);
		}
		for(Comment c : order.getComments()) {
			createOrderComment(order, c);
		}
		for(Payment p : order.getPayments()) {
			createOrderPayment(order, p);
		}
	}

	@Override
	public void createCustomer(Customer customer) throws Exception{
		PreparedStatement s = connector.prepareStatement("INSERT INTO customers (id, name, phone, address, province, postal_code, flags, discount) VALUES (?,?,?,?,?,?,?,?);");
		s.setInt(1, customer.getId());
		s.setString(2, customer.getName());
		s.setString(3, customer.getPhone());
		s.setString(4, customer.getAddress());
		s.setString(5, customer.getProvince());
		s.setString(6, customer.getPostalCode());
		s.setInt(7, customer.isTaxExempt() ? 1 : 0);
		s.setInt(8, customer.getDiscountPercentage());
		s.execute();
	}

	@Override
	public void createOrderComment(Order order, Comment comment) throws Exception{
		PreparedStatement s = connector.prepareStatement("INSERT INTO order_comments (id, parent, creator, type, value) VALUES (?,?,?,?,?);");
		s.setInt(1, comment.getId());
		s.setInt(2, order.getId());
		s.setInt(3, comment.getCreator().getId());
		s.setInt(4, comment.getType());
		s.setString(5, comment.getValue());
		s.execute();
	}

	@Override
	public void createOrderPayment(Order order, Payment payment) throws Exception{
		PreparedStatement s = connector.prepareStatement("INSERT INTO payments (id, parent, type, creator, amount, identifier, authorization) VALUES (?,?,?,?,?,?,?);");
		s.setInt(1, payment.getId());
		s.setInt(2, order.getId());
		s.setInt(3, payment.getType());
		s.setInt(4, payment.getCreator().getId());
		s.setInt(5, payment.getAmount());
		s.setString(6, payment.getIdentifier());
		s.setString(7, payment.getAuthorization());
		s.execute();
	}
	@Override
	public void createOrderLine(Order order, OrderLine line) throws Exception {
		PreparedStatement s = connector.prepareStatement("INSERT INTO order_lines (id, parent, product, count, original_price, price, override_reason) VALUES (?,?,?,?,?,?,?);");
		s.setInt(1, line.getId());
		s.setInt(2, order.getId());
		s.setInt(3, line.getProduct().getId());
		s.setInt(4, line.getCount());
		s.setInt(5, line.getOriginalPrice());
		s.setInt(6, line.getPrice());
		s.setInt(7, line.getOverrideReason());
		s.execute();
		Product p = line.getProduct();
		p.setCount(p.getCount()-line.getCount());
		updateProduct(p);
		if(line.getRet() != null)
			createReturn(line.getRet());
	}

	@Override
	public Order getOrder(int id) throws Exception{
		PreparedStatement s = connector.prepareStatement("SELECT * FROM orders WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		int type = result.getInt("type");
		int status = result.getInt("status");
		int subtotal = result.getInt("subtotal");
		int total = result.getInt("total");
		int user = result.getInt("user");
		int customer = result.getInt("customer");
		Timestamp creation_date = result.getTimestamp("creation_date");
		long time = creation_date.toInstant().getEpochSecond();
		List<OrderLine> lines = new ArrayList<OrderLine>();
		List<Comment> comments = new ArrayList<Comment>();
		List<Payment> payments = new ArrayList<Payment>();
		s = connector.prepareStatement("SELECT id FROM order_lines WHERE parent=?;");
		s.setInt(1, id);
		result = s.executeQuery();
		while(result.next()) {
			lines.add(getOrderLine(result.getInt("id")));
		}
		s = connector.prepareStatement("SELECT id FROM order_comments WHERE parent=?;");
		s.setInt(1, id);
		result = s.executeQuery();
		while(result.next()) {
			comments.add(getOrderComment(result.getInt("id")));
		}
		s = connector.prepareStatement("SELECT id FROM payments WHERE parent=?;");
		s.setInt(1, id);
		result = s.executeQuery();
		while(result.next()) {
			payments.add(getOrderPayment(result.getInt("id")));
		}
		return new Order(id, lines, subtotal, total, getCustomer(customer), comments, getUser(user), payments, type, status, time);
	}

	@Override
	public Customer getCustomer(int id) throws Exception{
		PreparedStatement s = connector.prepareStatement("SELECT * FROM customers WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		String name = result.getString("name");
		String phone = result.getString("phone");
		String address = result.getString("address");
		String province = result.getString("province");
		String postal = result.getString("postal_code");
		int flags = result.getInt("flags");
		int discount = result.getInt("discount");
		return new Customer(id, name, phone, address, province, postal, discount, (flags & 1) == 1);
	}

	@Override
	public void createUser(User user) throws Exception {
		PreparedStatement s = connector.prepareStatement("INSERT INTO users (id, name, username, access) VALUES (?,?,?,?);");
		s.setInt(1, user.getId());
		s.setString(2, user.getName());
		s.setString(3, user.getUsername());
		s.setInt(4, user.getAccess());
		s.execute();
	}

	@Override
	public User getUser(String username) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT * FROM users WHERE username=?;");
		s.setString(1, username);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		int id = result.getInt("id");
		String name = result.getString("name");
		int access = result.getInt("access");
		return new User(id, name, access, username);
	}

	@Override
	public OrderLine getOrderLine(int id) throws Exception {
		return getOrderLine(id, true);
	}
	public OrderLine getOrderLine(int id, boolean followRet) throws Exception{
		PreparedStatement s = connector.prepareStatement("SELECT * FROM order_lines WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		int product = result.getInt("product");
		int count = result.getInt("count");
		int origPrice = result.getInt("original_price");
		int price = result.getInt("price");
		int overrideReason = result.getInt("override_reason");
		Return ret = null;
		if(followRet)
			ret = getReturn(id);
		return new OrderLine(getProduct(product), count, origPrice, price, overrideReason, id, ret);
	}

	@Override
	public Comment getOrderComment(int id) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT * FROM order_comments WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		int creator = result.getInt("creator");
		int type = result.getInt("type");
		String value = result.getString("value");
		return new Comment(id, value, type, getUser(creator));
	}

	@Override
	public Payment getOrderPayment(int id) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT * FROM payments WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		int type = result.getInt("type");
		int creator = result.getInt("creator");
		int amount = result.getInt("amount");
		int parent = result.getInt("parent");
		String identifier = result.getString("identifier");
		String auth = result.getString("authorization");
		return new Payment(id, getUser(creator), type, amount, identifier, auth, parent);
	}

	@Override
	public Product getProduct(int id) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT * FROM products WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		String name = result.getString("name");
		String model = result.getString("model");
		String desc = result.getString("description");
		String url = result.getString("url");
		int price = result.getInt("price");
		String upc = result.getString("upc");
		int count = result.getInt("count");
		return new Product(id, name, model, desc, url, price, upc, count);
	}

	@Override
	public User getUser(int id) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT * FROM users WHERE id=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		String username = result.getString("username");
		String name = result.getString("name");
		int access = result.getInt("access");
		return new User(id, name, access, username);
	}

	@Override
	public int getNextOrderId() throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT id FROM orders WHERE 1 ORDER BY id DESC LIMIT 1;");
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return 0;
		}
		return result.getInt("id")+1;
	}

	@Override
	public int getNextCustomerId() throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT id FROM customers WHERE 1 ORDER BY id DESC LIMIT 1;");
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return 0;
		}
		return result.getInt("id")+1;
	}

	@Override
	public int getNextPaymentId() throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT id FROM payments WHERE 1 ORDER BY id DESC LIMIT 1;");
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return 0;
		}
		return result.getInt("id")+1;
	}

	@Override
	public int getNextOrderLineId() throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT id FROM order_lines WHERE 1 ORDER BY id DESC LIMIT 1;");
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return 0;
		}
		return result.getInt("id")+1;
	}

	@Override
	public int getNextOrderCommentId() throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT id FROM order_comments WHERE 1 ORDER BY id DESC LIMIT 1;");
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return 0;
		}
		return result.getInt("id")+1;
	}

	@Override
	public void updateProduct(Product product) throws Exception{
		PreparedStatement s = connector.prepareStatement("UPDATE products SET name=?,model=?,description=?,url=?,price=?,upc=?,count=? WHERE id=?;");
		s.setString(1, product.getName());
		s.setString(2, product.getModel());
		s.setString(3, product.getDesc());
		s.setString(4, product.getUrl());
		s.setInt(5, product.getPrice());
		s.setString(6, product.getUpc());
		s.setInt(7, product.getCount());
		s.setInt(8, product.getId());
		s.executeUpdate();
	}

	@Override
	public void createProduct(Product product) throws Exception {
		PreparedStatement s = connector.prepareStatement("INSERT INTO products (id, name, model, description, url, price, upc, count) VALUES (?,?,?,?,?,?,?,?);");
		s.setInt(1, product.getId());
		s.setString(2, product.getName());
		s.setString(3, product.getModel());
		s.setString(4, product.getDesc());
		s.setString(5, product.getUrl());
		s.setInt(6, product.getPrice());
		s.setString(7, product.getUpc());
		s.setInt(8, product.getCount());
		s.executeUpdate();
	}

	@Override
	public void createReturn(Return ret) throws Exception {
		PreparedStatement s = connector.prepareStatement("INSERT INTO returns (parent, original_order) VALUES (?,?);");
		s.setInt(1, ret.getParent().getId());
		s.setInt(2, ret.getOriginal().getId());
		s.executeUpdate();
	}

	@Override
	public Return getReturn(int id) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT * FROM returns WHERE parent=?;");
		s.setInt(1, id);
		ResultSet result = s.executeQuery();
		if(!result.next()) {
			return null;
		}
		int orig = result.getInt("original_order");
		return new Return(getOrderLine(id, false), getOrderLine(orig));
	}

	@Override
	public List<Payment> findPayment(String identifier) throws Exception {
		PreparedStatement s = connector.prepareStatement("SELECT id FROM payments WHERE identifier=?;");
		s.setString(1, identifier);
		ResultSet result = s.executeQuery();
		List<Payment> payments = new ArrayList<Payment>();
		while(result.next()) {
			payments.add(getOrderPayment(result.getInt("id")));
		}
		return payments;
	}
}
