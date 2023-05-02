package xyz.finlaym.pos.data;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Order extends JSONAble{
	public static final int TYPE_SALE = 0;
	public static final int TYPE_RETURN = 1;
	
	public static final int STATUS_READY = 1;
	public static final int STATUS_COMPLETE = 0;
	
	public static final int ORDER_CARRY = 1;
	public static final int ORDER_PICKUP = 0;
	
	private int id;
	private List<OrderLine> lines;
	private int subtotal;
	private int total;
	private Customer customer;
	private List<Comment> comments;
	private List<Payment> payments;
	private User creator;
	private int type;
	private int status;
	private long creation_date;
	private int register;
	
	public Order(int id, List<OrderLine> lines, int subtotal, int total, Customer customer, List<Comment> comments,
			User creator, List<Payment> payments, int type, int status, long creation_date, int register) {
		this.id = id;
		this.lines = lines;
		this.subtotal = subtotal;
		this.total = total;
		this.customer = customer;
		this.comments = comments;
		this.creator = creator;
		this.payments = payments;
		this.type = type;
		this.status = status;
		this.creation_date = creation_date;
		this.register = register;
	}
	public int getRegister() {
		return register;
	}
	public void setRegister(int register) {
		this.register = register;
	}
	public long getCreation_date() {
		return creation_date;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Payment> getPayments() {
		return payments;
	}
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<OrderLine> getLines() {
		return lines;
	}
	public void setLines(List<OrderLine> lines) {
		this.lines = lines;
	}
	public int getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(int subtotal) {
		this.subtotal = subtotal;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public User getCreator() {
		return creator;
	}
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	public boolean isPaid() {
		int currTotal = 0;
		for(Payment p : payments) {
			currTotal += p.getAmount();
		}
		return currTotal == total;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("paid", isPaid());
		self.put("type", type);
		self.put("creator", creator.toJSON());
		self.put("subtotal", subtotal);
		self.put("total", total);
		self.put("status", status);
		self.put("register", register);
		self.put("creation", creation_date);
		self.put("customer", customer.toJSON());
		JSONArray comments = new JSONArray();
		for(Comment c : this.comments) {
			comments.put(comments.length(), c.toJSON());
		}
		JSONArray payments = new JSONArray();
		for(Payment p : this.payments) {
			payments.put(payments.length(), p.toJSON());
		}
		JSONArray lines = new JSONArray();
		for(OrderLine l : this.lines) {
			lines.put(lines.length(), l.toJSON());
		}
		self.put("comments", comments);
		self.put("payments", payments);
		self.put("lines", lines);
		return self;
	}
}
