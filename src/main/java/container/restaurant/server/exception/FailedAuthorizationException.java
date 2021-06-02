package container.restaurant.server.exception;

public class FailedAuthorizationException extends RuntimeException {
    public FailedAuthorizationException(String message) {
        super(message);
    }
}
