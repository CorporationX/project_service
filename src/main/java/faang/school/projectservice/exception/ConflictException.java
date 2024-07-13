package faang.school.projectservice.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
