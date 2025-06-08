package faang.school.projectservice.exception;

public class FailedRequestException extends RuntimeException {
    public FailedRequestException(String message) {
        super(message);
    }
}