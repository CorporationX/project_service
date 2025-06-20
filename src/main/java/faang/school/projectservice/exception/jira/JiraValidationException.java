package faang.school.projectservice.exception.jira;

import faang.school.projectservice.exception.JiraIntegrationException;
import org.springframework.http.HttpStatus;

public class JiraValidationException extends JiraIntegrationException {
    public JiraValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
