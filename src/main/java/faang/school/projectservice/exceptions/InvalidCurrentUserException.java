package faang.school.projectservice.exceptions;

public class InvalidCurrentUserException extends RuntimeException{
    public InvalidCurrentUserException(String message) {
        super(message);
    }
}
