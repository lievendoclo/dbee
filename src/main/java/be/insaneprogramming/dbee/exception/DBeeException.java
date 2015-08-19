package be.insaneprogramming.dbee.exception;

public class DBeeException extends RuntimeException {
    public DBeeException(String message) {
        super(message);
    }

    public DBeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
