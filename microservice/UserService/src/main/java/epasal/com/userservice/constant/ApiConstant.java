package epasal.com.userservice.constant;

public class ApiConstant {
    public static final String BASE_API = "/api/v1/auth";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String MY_DETAILS = "/details";
    public static final String UPDATE_PASSWORD = "/password";
    public static final String REFRESH_TOKEN = "/refresh-token";
    public static final String LOGOUT = "/logout";
    public static final String ADDRESS = "/address";
    public static final String ADDRESS_ID = "/address/{id}";
    public static final String ADDRESS_EXISTS = "/address/exists";
    public static final String DELETE = "/address/{id}";
    public static final String VERIFICATION_CODE = "/code";
    public static final String VERIFY = "/verify";
    public static final String FULLNAME = "/{id}";
    public static final String EMAIL = "/email/{id}";

    public static final String ADMIN = "/api/v1/auth/admin";
    public static final String ADMIN_CODE = "/code";
    public static final String ADMIN_lOGIN = "/login";
    public static final String GET_USER_DETAILS = "/user/{id}";
    public static final String GET_ADDRESS = "/address/{id}";
}
