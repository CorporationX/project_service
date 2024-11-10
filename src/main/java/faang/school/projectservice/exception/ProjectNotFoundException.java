package faang.school.projectservice.exception;

public class ProjectNotFoundException extends StageServiceException {
    public ProjectNotFoundException(Long projectId) {
        super("Project not found with ID: " + projectId);
    }
}