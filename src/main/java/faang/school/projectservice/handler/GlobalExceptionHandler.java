package faang.school.projectservice.handler;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({RestClientException.class})
    public ErrorResponse handleRestClientException(Exception exception, HttpServletRequest request) {
        log.error("Error: {}", exception);
        return getErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ErrorResponse getErrorResponse(String url, HttpStatus status, String message) {
        return ErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .url(url)
                .status(status)
                .message(message)
                .build();
    }

}
