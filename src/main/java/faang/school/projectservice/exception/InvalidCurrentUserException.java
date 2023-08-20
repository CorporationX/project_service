package faang.school.projectservice.exception;

public class InvalidCurrentUserException extends RuntimeException{
    public InvalidCurrentUserException(String message) {
        super(message);
    }
}
