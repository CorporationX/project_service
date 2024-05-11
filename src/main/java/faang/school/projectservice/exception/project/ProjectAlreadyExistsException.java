package faang.school.projectservice.exception.project;

public class ProjectAlreadyExistsException extends ProjectRequestException {
    public ProjectAlreadyExistsException(String message) {
        super(message);
    }
}
