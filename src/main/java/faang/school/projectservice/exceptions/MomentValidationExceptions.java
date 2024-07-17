package faang.school.projectservice.exceptions;

public class MomentValidationExceptions extends RuntimeException {

    public MomentValidationExceptions(String message) {
        super(message);
    }

    public MomentValidationExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
