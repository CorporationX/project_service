package faang.school.projectservice.exception;

public class StorageSpaceExceededException extends RuntimeException {
    public StorageSpaceExceededException(String message) {
        super(message);
    }
}
