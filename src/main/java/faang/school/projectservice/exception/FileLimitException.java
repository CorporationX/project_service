package faang.school.projectservice.exception;

public class FileLimitException extends CustomException {
    public FileLimitException(ExceptionMessage message, long count) {
        super(message, count);
    }
}
