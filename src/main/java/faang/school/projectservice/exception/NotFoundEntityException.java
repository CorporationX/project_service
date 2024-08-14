package faang.school.projectservice.exception;

public class NotFoundEntityException extends RuntimeException{

    public NotFoundEntityException(String message) {
        super(message);
    }
}
