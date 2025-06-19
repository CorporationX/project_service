package faang.school.projectservice.exception.jira;

import faang.school.projectservice.exception.JiraIntegrationException;
import org.springframework.http.HttpStatus;

public class JiraConfigurationProblemException extends JiraIntegrationException {
    public JiraConfigurationProblemException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
