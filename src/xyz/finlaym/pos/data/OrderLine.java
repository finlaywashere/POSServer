package xyz.finlaym.pos.data;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderLine extends JSONAble{
	private Product product;
	private int count;
	private int originalPrice;
	private int price;
	private int overrideReason;
	private int id;
	private Return ret;
	private int countReturned;
	private List<Return> returned;
	
	public OrderLine(Product product, int count, int originalPrice, int price, int overrideReason, int id) {
		this(product,count,originalPrice,price,overrideReason,id,null,0,null);
	}
	public OrderLine(Product product, int count, int originalPrice, int price, int overrideReason, int id, Return ret, int countReturned, List<Return> returned) {
		this.product = product;
		this.count = count;
		this.originalPrice = originalPrice;
		this.price = price;
		this.overrideReason = overrideReason;
		this.id = id;
		this.ret = ret;
		this.countReturned = countReturned;
		this.returned = returned;
	}
	public List<Return> getReturned() {
		return returned;
	}
	public void setReturned(List<Return> returned) {
		this.returned = returned;
	}
	public int getCountReturned() {
		return countReturned;
	}
	public void setCountReturned(int countReturned) {
		this.countReturned = countReturned;
	}
	public Return getRet() {
		return ret;
	}
	public void setRet(Return ret) {
		this.ret = ret;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(int originalPrice) {
		this.originalPrice = originalPrice;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getOverrideReason() {
		return overrideReason;
	}
	public void setOverrideReason(int overrideReason) {
		this.overrideReason = overrideReason;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("product", product.toJSON());
		self.put("count", count);
		self.put("originalPrice", originalPrice);
		self.put("price", price);
		self.put("overrideReason", overrideReason);
		self.put("returnedCount", countReturned);
		if(this.ret != null)
			self.put("return", ret.toJSON());
		if(this.returned != null) {
			JSONArray arr = new JSONArray();
			for(Return r : returned) {
				arr.put(r.toJSON());
			}
			self.put("returned", arr);
		}
		return self;
	}
}
