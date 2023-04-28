package xyz.finlaym.pos.data;

import org.json.JSONObject;

public class Product extends JSONAble{
	private int id;
	private String name;
	private String model;
	private String desc;
	private String url;
	private int price;
	private String upc;
	private int count;
	
	public Product(int id, String name, String model, String desc, String url, int price, String upc, int count) {
		this.id = id;
		this.name = name;
		this.model = model;
		this.desc = desc;
		this.url = url;
		this.price = price;
		this.upc = upc;
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
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
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("name", name);
		self.put("model", model);
		self.put("desc", desc);
		self.put("url", url);
		return self;
	}
	
}
