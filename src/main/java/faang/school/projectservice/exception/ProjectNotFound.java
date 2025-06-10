package faang.school.projectservice.exception;

public class ProjectNotFound extends RuntimeException {
    public ProjectNotFound(Long id) {
        super(String.format("Project with id=%d not found", id));
    }
}
