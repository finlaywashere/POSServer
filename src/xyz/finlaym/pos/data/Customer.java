package xyz.finlaym.pos.data;

import org.json.JSONObject;

public class Customer extends JSONAble{
	private int id;
	private String name;
	private String phone;
	private String address;
	private String province;
	private String postalCode;
	private boolean taxExempt;
	private int discountPercentage;
	public Customer(int id, String name, String phone, String address, String province, String postalCode,
			int discountPercentage, boolean taxExempt) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.province = province;
		this.postalCode = postalCode;
		this.discountPercentage = discountPercentage;
		this.taxExempt = taxExempt;
	}
	public boolean isTaxExempt() {
		return taxExempt;
	}
	public void setTaxExempt(boolean taxExempt) {
		this.taxExempt = taxExempt;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public int getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("id", id);
		self.put("name", name);
		self.put("phone", phone);
		self.put("address", address);
		self.put("province", province);
		self.put("postal", postalCode);
		self.put("discount", discountPercentage);
		self.put("flags", taxExempt ? 1 : 0);
		return self;
	}
}
