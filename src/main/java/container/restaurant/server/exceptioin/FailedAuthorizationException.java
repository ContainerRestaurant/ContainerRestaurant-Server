package container.restaurant.server.exceptioin;

public class FailedAuthorizationException extends RuntimeException {
    public FailedAuthorizationException(String message) {
        super(message);
    }
}
