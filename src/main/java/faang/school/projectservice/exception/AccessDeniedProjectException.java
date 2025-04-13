package faang.school.projectservice.exception;

public class AccessDeniedProjectException extends RuntimeException {
    public AccessDeniedProjectException(String message) {
        super(message);
    }
}
