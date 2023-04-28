package xyz.finlaym.pos.server.connector;

import java.util.List;

import xyz.finlaym.pos.data.Comment;
import xyz.finlaym.pos.data.Customer;
import xyz.finlaym.pos.data.Order;
import xyz.finlaym.pos.data.OrderLine;
import xyz.finlaym.pos.data.Payment;
import xyz.finlaym.pos.data.Product;
import xyz.finlaym.pos.data.Return;
import xyz.finlaym.pos.data.User;

public abstract class DataConnection {
	public abstract void createOrder(Order order) throws Exception;
	public abstract void createCustomer(Customer customer) throws Exception;
	public abstract void createOrderComment(Order order, Comment comment) throws Exception;
	public abstract void createOrderPayment(Order order, Payment payment) throws Exception;
	public abstract void createOrderLine(Order order, OrderLine line) throws Exception;
	public abstract void createUser(User user) throws Exception;
	public abstract void createProduct(Product product) throws Exception;
	public abstract void createReturn(Return ret) throws Exception;
	public abstract Order getOrder(int id) throws Exception;
	public abstract OrderLine getOrderLine(int id) throws Exception;
	public abstract Comment getOrderComment(int id) throws Exception;
	public abstract Payment getOrderPayment(int id) throws Exception;
	public abstract Customer getCustomer(int id) throws Exception;
	public abstract User getUser(String username) throws Exception;
	public abstract User getUser(int id) throws Exception;
	public abstract Product getProduct(int id) throws Exception;
	public abstract Return getReturn(int id) throws Exception;
	public abstract int getNextOrderId() throws Exception;
	public abstract int getNextCustomerId() throws Exception;
	public abstract int getNextPaymentId() throws Exception;
	public abstract int getNextOrderLineId() throws Exception;
	public abstract int getNextOrderCommentId() throws Exception;
	public abstract List<Return> getReturnedLines(int id) throws Exception;
	public abstract void updateProduct(Product product) throws Exception;
	public abstract void markReturned(OrderLine line) throws Exception;
	public abstract List<Payment> findPayment(String identifier) throws Exception;
}
