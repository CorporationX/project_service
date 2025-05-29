package faang.school.projectservice.exception;

public class StorageSizeExceedException extends RuntimeException {
    public StorageSizeExceedException(String message) {
        super(message);
    }
}