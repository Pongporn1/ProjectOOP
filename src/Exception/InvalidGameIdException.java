package Exception;

public class InvalidGameIdException extends RuntimeException {
    public InvalidGameIdException(String message) {
        super(message);
    }
}
