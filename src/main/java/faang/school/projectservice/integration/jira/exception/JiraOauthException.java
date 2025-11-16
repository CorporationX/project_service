package faang.school.projectservice.integration.jira.exception;

import lombok.Getter;

@Getter
public class JiraOauthException extends RuntimeException {

    private final String errorCode;
    private final String errorDescription;

    public JiraOauthException(String message) {
        super(message);
        this.errorCode = null;
        this.errorDescription = null;
    }

    public JiraOauthException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.errorDescription = null;
    }

    /**
     * Конструктор для Oauth error response
     *
     * @param errorCode Oauth error code (invalid_grant, invalid_client, etc.)
     * @param errorDescription Описание ошибки от Oauth сервера
     */
    public JiraOauthException(String errorCode, String errorDescription) {
        super("Oauth error: " + errorCode + " - " + errorDescription);
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

}
