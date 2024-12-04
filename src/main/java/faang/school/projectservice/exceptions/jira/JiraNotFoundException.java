package faang.school.projectservice.exceptions.jira;

public class JiraNotFoundException extends RuntimeException {

    public JiraNotFoundException(String responseBody) {
        super(String.format("Jira not found error: %s", responseBody));
    }
}
