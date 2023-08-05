package faang.school.projectservice.exception;

public class DataValidException extends RuntimeException{
    public DataValidException(String message) {
        super(message);
    }

    public DataValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
