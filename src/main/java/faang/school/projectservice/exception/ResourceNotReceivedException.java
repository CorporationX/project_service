package faang.school.projectservice.exception;

public class ResourceNotReceivedException extends RuntimeException {
    public ResourceNotReceivedException(String message) {
        super(message);
    }
}