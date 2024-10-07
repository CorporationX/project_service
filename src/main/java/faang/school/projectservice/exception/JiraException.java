package faang.school.projectservice.exception;

import faang.school.projectservice.dto.ErrorResponse;
import lombok.Getter;

@Getter
public class JiraException extends RuntimeException {
    private final ErrorResponse errorResponse;
    private final Integer httpCode;

    public JiraException(ErrorResponse errorResponse, Integer httpCode) {
        super("Error Jira API");
        this.errorResponse = errorResponse;
        this.httpCode = httpCode;
    }
}

