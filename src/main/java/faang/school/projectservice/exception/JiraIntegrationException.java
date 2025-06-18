package faang.school.projectservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JiraIntegrationException extends RuntimeException {
    private final HttpStatus status;

    public JiraIntegrationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public JiraIntegrationException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}



