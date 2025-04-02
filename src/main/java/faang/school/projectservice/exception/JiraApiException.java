package faang.school.projectservice.exception;

public class JiraApiException extends RuntimeException {
    public JiraApiException(String message) {
        super(String.format("Jira API exception: %s", message));
    }
}
