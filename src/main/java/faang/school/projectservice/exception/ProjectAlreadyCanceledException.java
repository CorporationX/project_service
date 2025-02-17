package faang.school.projectservice.exception;

public class ProjectAlreadyCanceledException extends RuntimeException {

    public ProjectAlreadyCanceledException(String message) {
        super(message);
    }
}
