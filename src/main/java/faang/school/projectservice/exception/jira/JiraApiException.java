package faang.school.projectservice.exception.jira;

public class JiraApiException extends RuntimeException {
    public JiraApiException(String message) {
        super(message);
    }
}
