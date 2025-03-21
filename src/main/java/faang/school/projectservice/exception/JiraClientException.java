package faang.school.projectservice.exception;

import lombok.Getter;
import org.springframework.web.ErrorResponse;

@Getter
public class JiraClientException extends RuntimeException {
    private final ErrorResponse errorResponse;
    private final Integer httpCode;

    public JiraClientException(ErrorResponse errorResponse, Integer httpCode) {
        super("Error in Jira API");
        this.errorResponse = errorResponse;
        this.httpCode = httpCode;
    }
}
