package faang.school.projectservice.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(Long id) {
        super(String.format("Project with id=%d not found", id));
    }
}
