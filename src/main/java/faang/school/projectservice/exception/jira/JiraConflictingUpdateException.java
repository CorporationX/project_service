package faang.school.projectservice.exception.jira;

import faang.school.projectservice.exception.JiraIntegrationException;
import org.springframework.http.HttpStatus;

public class JiraConflictingUpdateException extends JiraIntegrationException {
    public JiraConflictingUpdateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
