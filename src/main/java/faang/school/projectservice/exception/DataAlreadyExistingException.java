package faang.school.projectservice.exception;

public class DataAlreadyExistingException extends RuntimeException{
    public DataAlreadyExistingException(String message) {
        super(message);
    }
}
