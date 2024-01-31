package faang.school.projectservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;
    @ExceptionHandler
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleViolationException(ValidationException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleSecurityException(SecurityException ex) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String defaultMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), defaultMessage));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleFeignException(FeignException ex) {
        HttpStatus httpStatus = HttpStatus.valueOf(ex.status());
        String responseBody = ex.contentUTF8();
        ResponseError responseError;
        try {
            responseError = objectMapper.readValue(responseBody, ResponseError.class);
        } catch (JsonProcessingException e) {
            responseError = new ResponseError(
                    httpStatus.value(),
                    "An error occurred while processing the request"
            );
        }
        return ResponseEntity
                .status(httpStatus)
                .body(responseError);
    }
}