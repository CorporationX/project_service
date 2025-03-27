package faang.school.projectservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class JiraClientException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public JiraClientException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
