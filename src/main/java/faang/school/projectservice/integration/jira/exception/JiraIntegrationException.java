package faang.school.projectservice.integration.jira.exception;

public class JiraIntegrationException extends RuntimeException {
    
    public JiraIntegrationException(String message) {
        super(message);
    }
    
    public JiraIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

