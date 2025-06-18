package faang.school.projectservice.exception.jira;

import faang.school.projectservice.exception.JiraIntegrationException;
import org.springframework.http.HttpStatus;

public class JiraPermissionDeniedException extends JiraIntegrationException {
    public JiraPermissionDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
