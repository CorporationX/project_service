package faang.school.projectservice.exception;

import faang.school.projectservice.dto.error.ErrorResponse;
import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
            log.error("ValidationException occurred: {}", error.getDefaultMessage());
        });
        return errors;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handlerProjectNotFoundException(ProjectNotFoundException ex) {
        log.error("ProjectNotFoundException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
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

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        return getErrorResponse(ex, BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleFeignException(FeignException ex) {
        return getErrorResponse(ex, NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return getErrorResponse(ex, FORBIDDEN);
    }

    @ExceptionHandler({
            jakarta.persistence.EntityNotFoundException.class,
            faang.school.projectservice.exception.EntityNotFoundException.class
    })
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        return getErrorResponse(ex, NOT_FOUND);
    }

    @ExceptionHandler(InternshipGetInternsIdException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleInternshipGetInternsIdException(InternshipGetInternsIdException ex) {
        return getErrorResponse(ex, NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInputOutputException(IOException ex) {
        return getErrorResponse(ex, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleBindingException(ServletRequestBindingException ex) {
        return getErrorResponse(ex, BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMultipartException(MultipartException ex) {
        return getErrorResponse(ex, BAD_REQUEST);
    }

    @ExceptionHandler(JiraApiException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleJiraApiException(JiraApiException ex) {
        return getErrorResponse(ex, BAD_REQUEST);
    }

    @ExceptionHandler(CryptoOperationException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handleGeneralSecurityException(CryptoOperationException ex) {
        return getErrorResponse(ex, FORBIDDEN);
    }

    private ErrorResponse getErrorResponse(Exception ex, HttpStatus statusCode) {
        log.error("{}", ex.toString());
        return ErrorResponse.builder()
                .statusCode(statusCode.name())
                .message(ex.getMessage())
                .build();
    }
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<String> handleInvalidFileException(InvalidFileException ex) {
        log.error("InvalidFileException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String > handleFileProcessingException(FileProcessingException ex) {
        log.error("FileProcessingException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(CoverMaxSizeException.class)
    public ResponseEntity<String> handleCoverMaxSizeException(CoverMaxSizeException ex) {
        log.error("CoverMaxSizeException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(FileLimitException.class)
    public ResponseEntity<String> handleFileLimitException(FileLimitException ex) {
        log.error("FileLimitException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
