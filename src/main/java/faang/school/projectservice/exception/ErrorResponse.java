package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public ErrorResponse(HttpStatus httpStatus, Exception exception, WebRequest request) {
        timestamp = LocalDateTime.now();
        status = httpStatus.value();
        error = httpStatus.getReasonPhrase();
        message = exception.getMessage();
        path = request.getDescription(false).split("=", 2)[1];
    }
}
