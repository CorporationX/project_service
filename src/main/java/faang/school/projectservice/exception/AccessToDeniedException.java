package faang.school.projectservice.exception;

public class AccessToDeniedException extends RuntimeException {
    public AccessToDeniedException(String message) {
        super(message);
    }
}
