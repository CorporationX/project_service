package faang.school.projectservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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

}
