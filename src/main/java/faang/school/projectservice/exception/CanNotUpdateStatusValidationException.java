package faang.school.projectservice.exception;

public class CanNotUpdateStatusValidationException extends RuntimeException {
    public CanNotUpdateStatusValidationException() {
        super("Finished internship can't be update");
    }
}
