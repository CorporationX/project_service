package faang.school.projectservice.exception.jira;

public class JiraNotFoundException extends RuntimeException {
    public JiraNotFoundException(String message) {
        super(message);
    }
}
