package danila.backendservice.exception;

public class OutboxEventSerializationException extends RuntimeException {
    public OutboxEventSerializationException(String message, Exception e) {
        super(message, e);
    }
}
