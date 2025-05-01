package faang.school.projectservice.exception;

public class UnauthorizedAccessException extends IllegalArgumentException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
