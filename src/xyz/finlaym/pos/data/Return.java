package xyz.finlaym.pos.data;

import org.json.JSONObject;

public class Return extends JSONAble{

	private OrderLine parent;
	private OrderLine original;
	
	public Return(OrderLine parent, OrderLine original) {
		super();
		this.parent = parent;
		this.original = original;
	}
	public OrderLine getParent() {
		return parent;
	}
	public void setParent(OrderLine parent) {
		this.parent = parent;
	}
	public OrderLine getOriginal() {
		return original;
	}
	public void setOriginal(OrderLine original) {
		this.original = original;
	}
	@Override
	public JSONObject toJSON() {
		JSONObject self = new JSONObject();
		self.put("parent", parent);
		self.put("original", original);
		return self;
	}

}
