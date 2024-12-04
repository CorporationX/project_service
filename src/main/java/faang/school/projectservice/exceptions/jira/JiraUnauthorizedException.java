package faang.school.projectservice.exceptions.jira;

public class JiraUnauthorizedException extends RuntimeException {

    public JiraUnauthorizedException() {
        super();
    }

    public JiraUnauthorizedException(String message) {
        super(message);
    }

    public JiraUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
