package faang.school.projectservice.exceptions;

public class NotFoundElementInDataBase extends RuntimeException{

    public NotFoundElementInDataBase(String message) {
        super(message);
    }

    public NotFoundElementInDataBase(String message, Throwable cause) {
        super(message, cause);
    }
}
