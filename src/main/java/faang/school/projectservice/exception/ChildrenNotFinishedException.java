package faang.school.projectservice.exception;

public class ChildrenNotFinishedException extends RuntimeException{
    public final String message;

    public ChildrenNotFinishedException(String message) {
        super(message);
        this.message = message;
    }
}
