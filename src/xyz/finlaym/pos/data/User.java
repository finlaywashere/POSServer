package xyz.finlaym.pos.data;

import org.json.JSONObject;

public class User extends JSONAble{
	private int id;
	private String name;
	private int access;
	private String username;
	
	public User(int id, String name, int access, String username) {
		this.id = id;
		this.name = name;
		this.access = access;
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAccess() {
		return access;
	}
	public void setAccess(int access) {
		this.access = access;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("name", name);
		self.put("access", access);
		self.put("username", username);
		return self;
	}
}
