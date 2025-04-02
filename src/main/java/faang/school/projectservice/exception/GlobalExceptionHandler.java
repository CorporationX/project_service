package faang.school.projectservice.exception;

import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import faang.school.projectservice.dto.error.ErrorResponse;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Integer BAD_REQUEST_STATUS = 400;
    private static final Integer FORBIDDEN_STATUS = 403;
    private static final Integer NOT_FOUND_STATUS = 404;
    private static final Integer INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final String ERROR_EXAMPLE = "[{}] Status = {} | Message = {}";

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        return ResponseEntity.status(NOT_FOUND_STATUS).body(getErrorResponse(ex, NOT_FOUND_STATUS));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(FORBIDDEN_STATUS).body(getErrorResponse(ex, FORBIDDEN_STATUS));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException ex) {
        return ResponseEntity.status(BAD_REQUEST_STATUS).body(getErrorResponse(ex, BAD_REQUEST_STATUS));
    }

    @ExceptionHandler({
            jakarta.persistence.EntityNotFoundException.class,
            faang.school.projectservice.exception.EntityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND_STATUS).body(getErrorResponse(ex, NOT_FOUND_STATUS));
    }

    @ExceptionHandler(InternshipGetInternsIdException.class)
    public ResponseEntity<ErrorResponse> handleInternshipGetInternsIdException(InternshipGetInternsIdException ex) {
        return ResponseEntity.status(NOT_FOUND_STATUS).body(getErrorResponse(ex, NOT_FOUND_STATUS));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleInputOutputException(IOException ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR_STATUS)
                .body(getErrorResponse(ex, INTERNAL_SERVER_ERROR_STATUS));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(ServletRequestBindingException ex) {
        return ResponseEntity.status(BAD_REQUEST_STATUS).body(getErrorResponse(ex, BAD_REQUEST_STATUS));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
        return ResponseEntity.status(BAD_REQUEST_STATUS).body(getErrorResponse(ex, BAD_REQUEST_STATUS));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(getErrorResponse(ex, ex.getStatusCode().value()));
    }

    @ExceptionHandler(JiraApiException.class)
    public ResponseEntity<ErrorResponse> handleJiraApiException(JiraApiException ex) {
        return ResponseEntity.status(BAD_REQUEST_STATUS).body(getErrorResponse(ex, BAD_REQUEST_STATUS));
    }

    private ErrorResponse getErrorResponse(Exception ex, Integer statusCode) {
        log.error(ERROR_EXAMPLE, ex.toString(), statusCode, ex.getMessage());
        return ErrorResponse.builder()
                .title(ex.toString())
                .details(ex.getMessage())
                .build();
    }
}
