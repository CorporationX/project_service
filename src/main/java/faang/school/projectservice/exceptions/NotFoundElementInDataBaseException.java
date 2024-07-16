package faang.school.projectservice.exceptions;

public class NotFoundElementInDataBaseException extends RuntimeException{

    public NotFoundElementInDataBaseException(String message) {
        super(message);
    }

    public NotFoundElementInDataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
