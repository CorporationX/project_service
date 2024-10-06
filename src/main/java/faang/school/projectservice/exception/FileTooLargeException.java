package faang.school.projectservice.exception;

public class FileTooLargeException extends RuntimeException{
    public FileTooLargeException(String message) {
        super(message);
    }
}
