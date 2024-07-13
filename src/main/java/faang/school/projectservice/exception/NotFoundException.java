package faang.school.projectservice.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
