package faang.school.projectservice.exception;

public class ProjectAlreadyExistsException extends RuntimeException{
    public ProjectAlreadyExistsException(String message) {
        super(message);
    }
}
