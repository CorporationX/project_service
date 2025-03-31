package faang.school.projectservice.exception;

public class CustomException extends RuntimeException {

    public CustomException(ExceptionMessage exceptionMessage, Object... args) {
        super(exceptionMessage.formatMessage(args));
    }
}
