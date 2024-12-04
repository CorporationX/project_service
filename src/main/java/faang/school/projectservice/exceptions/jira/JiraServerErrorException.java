package faang.school.projectservice.exceptions.jira;

import org.springframework.http.HttpStatus;

public class JiraServerErrorException extends RuntimeException {

    public JiraServerErrorException(HttpStatus httpStatus) {
        super(String.format("Server error %s", httpStatus));
    }

    public JiraServerErrorException() {
        super();
    }

    public JiraServerErrorException(String message) {
        super(message);
    }
}
