package epasal.com.orderservice.constants;

public class ApiConstants {
    public static final String BASE_API = "/api/v1/orders";
    public static final String GET_ORDER_BY_ID = "/{orderId}";
    public static final String CANCEL = "/cancel/{orderId}";
    public static final String PLACE_ORDER = "/place";
    public static final String GET_ORDERS_BY_STATUS = "/status";
    public static final String GET_ORDERS_BY_ID_ADMIN = "/admin/{id}";
    public static final String GET_PAYMENT_BY_ID_ADMIN = "/payment/admin/{id}";
    public static final String GET_ALL_ORDERS = "/all";
    public static final String HISTORY = "/history/{id}";
    public static final String PAYMENT_SUCCESS = "/payment/success";
    public static final String PAYMENT_FAILURE = "/payment/failure";
    public static final String PAYMENT = "/payment/{orderId}";
    public static final String ALL_PAYMENT = "/payment";
    public static final String KHALTI = "/payment/khalti";
    public static final String REPAY = "/repay";
    public static final String CONFLICT = "/conflict";
    public static final String DELETE = "/delete/{orderId}";

}
