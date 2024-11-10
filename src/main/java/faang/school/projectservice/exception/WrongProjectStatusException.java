package faang.school.projectservice.exception;

public class WrongProjectStatusException extends StageServiceException {
    public WrongProjectStatusException(Long projectId) {
        super("Project with ID " + projectId + " has a wrong status");
    }
}