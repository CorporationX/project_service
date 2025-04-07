package faang.school.projectservice.exceptions;

public class StorageLimitExceededException extends IllegalStateException {
    public StorageLimitExceededException(String message) {
        super(message);
    }
}
