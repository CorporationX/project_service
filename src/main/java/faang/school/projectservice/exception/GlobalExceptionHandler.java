package faang.school.projectservice.exception;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "IllegalArgument");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Validation Failed");
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidation(DataValidationException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Data Validation Failed");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Not Found");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "ForbiddenException");
    }

    @ExceptionHandler(DuplicateTitleException.class)
    public ResponseEntity<Object> handleDuplicateTitleException(DuplicateTitleException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, "DuplicateTitleException");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Data Validation Failed");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @ExceptionHandler(StorageSizeExceededException.class)
    public ResponseEntity<Object> handleStorageSizeExceededException(StorageSizeExceededException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Storage Size Exceeded");
    }

    @ExceptionHandler(ResourceHandlingException.class)
    public ResponseEntity<Object> handleResourceHandlingException(ResourceHandlingException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Resource Handling Error");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, "Access Denied");
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Object> handleTaskNotFoundException(TaskNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Not Found Error");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<String> handleImageProcessingException(ImageProcessingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status, String error) {
        return buildErrorResponse(cleanMessage(ex), status, error);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0));
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    private String cleanMessage(Exception ex) {
        String message = ex.getMessage();
        if (message != null) {
            int index = message.indexOf("(");
            if (index != -1) {
                message = message.substring(0, index).trim();
            }
        }
        return message;
    }
}
