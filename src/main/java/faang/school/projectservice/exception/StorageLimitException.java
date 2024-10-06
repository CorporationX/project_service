package faang.school.projectservice.exception;

public class StorageLimitException extends RuntimeException {
    public StorageLimitException(String message) {
        super(message);
    }

    public StorageLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
