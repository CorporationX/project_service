package faang.school.projectservice.exception;

public class PermissionDeniedException extends CustomException {
    public PermissionDeniedException() {
        super(ExceptionMessage.PERMISSION_DENIED);
    }
}
