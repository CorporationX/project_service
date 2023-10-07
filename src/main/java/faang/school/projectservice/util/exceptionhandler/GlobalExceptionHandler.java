package faang.school.projectservice.util.exceptionhandler;

import faang.school.projectservice.dto.response.ErrorResponse;
import faang.school.projectservice.util.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        log.error("Error has been occurred when validating inputs: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(DataValidationException e) {
        log.error("Error has been occurred when validating inputs: {}", e.getMessage(), e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
