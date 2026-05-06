package epasal.com.userservice.exception;

public class BadCodeRequestException extends RuntimeException {
    public BadCodeRequestException(String message) {
        super(message);
    }
}
