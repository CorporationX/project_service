package faang.school.projectservice.exception;

public class FileSizeLimitException extends IllegalArgumentException {
    public FileSizeLimitException(String message) {
        super(message);
    }
}
