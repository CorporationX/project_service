package faang.school.projectservice.exception;

public class PermissionDeniedException extends CustomException {
    public PermissionDeniedException(ExceptionMessage message) {
        super(message);
    }
}
