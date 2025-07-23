package dataaccess.exceptions;

public class InvalidPasswordException extends DataAccessException {
    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable ex) {
      super(message, ex);
    }
}
