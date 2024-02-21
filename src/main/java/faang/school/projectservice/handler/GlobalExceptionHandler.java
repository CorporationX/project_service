package faang.school.projectservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException", ex);
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException", ex);
        return new ResponseError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleViolationException(ValidationException ex) {
        log.error("ValidationException", ex);
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseError handleSecurityException(SecurityException ex) {
        log.error("SecurityException", ex);
        return new ResponseError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String defaultMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("MethodArgumentNotValidException", ex);
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), defaultMessage);
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
        log.error("FeignException", ex);
        return ResponseEntity
                .status(httpStatus)
                .body(responseError);
    }
}