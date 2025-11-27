package faang.school.projectservice.integration.jira.exception;

public class ProjectIntegrationException extends RuntimeException {

    public ProjectIntegrationException(String message) {
        super(message);
    }

    public ProjectIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}


