package faang.school.projectservice.exception;

public class StorageSizeExceededException extends RuntimeException {
    public StorageSizeExceededException(String message) {
        super(message);
    }
}
