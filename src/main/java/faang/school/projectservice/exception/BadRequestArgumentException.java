package faang.school.projectservice.exception;

public class BadRequestArgumentException extends RuntimeException {
    public BadRequestArgumentException(String message) {
        super(message);
    }
}
