package faang.school.projectservice.exception;

import faang.school.projectservice.dto.error.ErrorResponse;
import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
            log.error("ValidationException occurred: {}", error.getDefaultMessage());
        });
        return errors;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(FORBIDDEN_STATUS).body(getErrorResponse(ex, FORBIDDEN_STATUS));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handlerProjectNotFoundException(ProjectNotFoundException ex) {
        log.error("ProjectNotFoundException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
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

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<String> handlePermissionDeniedException(PermissionDeniedException ex) {
        log.error("PermissionDeniedException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CampaignNotFoundException.class)
    public ResponseEntity<String> handleCampaignNotFoundException(CampaignNotFoundException ex) {
        log.error("CampaignNotFoundException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CampaignCreatorModificationException.class)
    public ResponseEntity<String> handleCampaignCreatorModificationException(CampaignCreatorModificationException ex) {
        log.error("CampaignCreatorModificationException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DateParseException.class)
    public ResponseEntity<String> handleDateParseException(DateParseException ex) {
        log.error("DateParseException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EmptyFilterException.class)
    public ResponseEntity<String> handleEmptyFilterException(EmptyFilterException ex) {
        log.error("EmptyFilterException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
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

    @ExceptionHandler(CryptoOperationException.class)
    public ResponseEntity<ErrorResponse> handleGeneralSecurityException(CryptoOperationException ex) {
        return ResponseEntity.status(FORBIDDEN_STATUS).body(getErrorResponse(ex, FORBIDDEN_STATUS));
    }

    private ErrorResponse getErrorResponse(Exception ex, Integer statusCode) {
        log.error(ERROR_EXAMPLE, ex.toString(), statusCode, ex.getMessage());
        return ErrorResponse.builder()
                .title(ex.toString())
                .details(ex.getMessage())
                .build();
    }
}
