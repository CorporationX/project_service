package faang.school.projectservice.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Object... args) {
        super(String.format(message, args));
    }
}
