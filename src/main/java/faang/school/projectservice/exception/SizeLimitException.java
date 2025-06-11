package faang.school.projectservice.exception;

public class SizeLimitException extends RuntimeException {
    public SizeLimitException(String message) {
        super(message);
    }
}
