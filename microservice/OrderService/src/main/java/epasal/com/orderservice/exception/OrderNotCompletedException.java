package epasal.com.orderservice.exception;

public class OrderNotCompletedException extends RuntimeException {
    public OrderNotCompletedException(String message) {
        super(message);
    }
}
