package xyz.finlaym.pos.data;

import org.json.JSONObject;

public class Comment extends JSONAble{
	public static final int TYPE_ORDER = 0;
	
	private int id;
	private String value;
	private int type;
	private User creator;
	public Comment(int id, String value, int type, User creator) {
		this.id = id;
		this.value = value;
		this.type = type;
		this.creator = creator;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public User getCreator() {
		return creator;
	}
	public void setCreator(User creator) {
		this.creator = creator;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("value", value);
		self.put("type", type);
		self.put("creator", creator.toJSON());
		return self;
	}
}
