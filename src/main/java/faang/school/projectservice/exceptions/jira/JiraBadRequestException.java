package faang.school.projectservice.exceptions.jira;

public class JiraBadRequestException extends RuntimeException {

    public JiraBadRequestException() {
        super();
    }

    public JiraBadRequestException(String responseBody) {
        super(String.format("Jira bad request error: %s", responseBody));
    }
}
