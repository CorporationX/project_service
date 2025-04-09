package faang.school.projectservice.exception;

public class JiraConnectionException extends RuntimeException {
    public JiraConnectionException(String message, Object... args) {
        super(String.format(message, args));
    }
}
