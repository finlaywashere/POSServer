package xyz.finlaym.pos.data;

import org.json.JSONObject;

public class Payment extends JSONAble{
	public static final int TYPE_CASH = 0;
	public static final int TYPE_PENNYROUND = 1;
	public static final int TYPE_CREDIT = 2;
	public static final int TYPE_DEBIT = 3;
	public static final int TYPE_GIFTCARD = 4;
	public static final int TYPE_ACCOUNT = 5;
	
	private int id;
	private User creator;
	private int type;
	private int amount;
	private String identifier;
	private String authorization;
	private int order;
	
	public Payment(int id, User creator, int type, int amount, String identifier, String auth, int order) {
		this.id = id;
		this.creator = creator;
		this.type = type;
		this.amount = amount;
		this.identifier = identifier;
		this.authorization = auth;
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getCreator() {
		return creator;
	}
	public void setCreator(User creator) {
		this.creator = creator;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("amount", amount);
		self.put("creator", creator.toJSON());
		self.put("type", type);
		self.put("identifier", identifier);
		self.put("authorization", authorization);
		return self;
	}
}
