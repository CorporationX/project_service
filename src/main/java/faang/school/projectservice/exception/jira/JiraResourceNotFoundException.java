package faang.school.projectservice.exception.jira;

import faang.school.projectservice.exception.JiraIntegrationException;
import org.springframework.http.HttpStatus;

public class JiraResourceNotFoundException extends JiraIntegrationException {
    public JiraResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
