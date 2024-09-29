package faang.school.projectservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

public class ApiException extends RuntimeException {
    @Getter
    private HttpStatusCode httpStatus;

    public ApiException(String message, HttpStatusCode httpStatus, Object... args) {
        super(String.format(message, args));
        this.httpStatus = httpStatus;
    }
}
