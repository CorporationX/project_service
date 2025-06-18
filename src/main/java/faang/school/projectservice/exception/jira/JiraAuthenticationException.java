package faang.school.projectservice.exception.jira;

import faang.school.projectservice.exception.JiraIntegrationException;
import org.springframework.http.HttpStatus;

public class JiraAuthenticationException extends JiraIntegrationException {
    public JiraAuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
