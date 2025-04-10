package faang.school.projectservice.exception;

public class StorageLimitExceededException extends IllegalStateException {
    public StorageLimitExceededException(String message) {
        super(message);
    }
}
