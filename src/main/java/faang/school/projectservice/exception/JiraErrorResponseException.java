package faang.school.projectservice.exception;

import faang.school.projectservice.dto.jira.JiraDto;

public class JiraErrorResponseException extends RuntimeException{
    private final JiraDto errorResponse;

    public JiraErrorResponseException(String message, JiraDto errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

    public JiraDto getErrorResponse() {
        return errorResponse;
    }
}
