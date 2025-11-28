package faang.school.projectservice.integration.jira.exception;

import lombok.Getter;

@Getter
public class JiraOAuthException extends RuntimeException {

    private final String errorCode;
    private final String errorDescription;

    public JiraOAuthException(String message) {
        super(message);
        this.errorCode = null;
        this.errorDescription = null;
    }

    public JiraOAuthException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.errorDescription = null;
    }

    /**
     * Конструктор для OAuth error response
     *
     * @param errorCode OAuth error code (invalid_grant, invalid_client, etc.)
     * @param errorDescription Описание ошибки от OAuth сервера
     */
    public JiraOAuthException(String errorCode, String errorDescription) {
        super(String.format("OAuth error: %s - %s", errorCode, errorDescription));
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

}
