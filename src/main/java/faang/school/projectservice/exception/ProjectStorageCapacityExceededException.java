package faang.school.projectservice.exception;

public class ProjectStorageCapacityExceededException extends RuntimeException {
    public ProjectStorageCapacityExceededException(String message) {
        super(message);
    }
}