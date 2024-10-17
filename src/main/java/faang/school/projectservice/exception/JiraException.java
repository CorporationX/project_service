package faang.school.projectservice.exception;

import org.springframework.http.HttpStatusCode;

public class JiraException extends RuntimeException {
    private HttpStatusCode status;

    public JiraException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
