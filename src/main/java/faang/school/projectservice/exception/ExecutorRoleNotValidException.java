package faang.school.projectservice.exception;

public class ExecutorRoleNotValidException extends StageServiceException {
    public ExecutorRoleNotValidException(Long executorId, Long stageId) {
        super("Executor with ID " + executorId + " does not have a valid role for stage " + stageId);
    }
}