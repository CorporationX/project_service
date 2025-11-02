package faang.school.projectservice.exception.vacancy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Map<Class<? extends Exception>, HttpStatus> exceptionMappings = Map.of(
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            ForbiddenException.class, HttpStatus.FORBIDDEN,
            EntityNotFoundException.class, HttpStatus.NOT_FOUND,
            IllegalStateException.class, HttpStatus.CONFLICT,
            IllegalArgumentException.class, HttpStatus.CONFLICT
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        HttpStatus status = exceptionMappings.getOrDefault(ex.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR);

        if (ex instanceof MethodArgumentNotValidException validationEx) {
            List<String> errors = validationEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            log.error("Validation error: {}", errors);
            return ResponseEntity.status(status)
                    .body(new ErrorResponse(LocalDateTime.now(), "Invalid data", errors));
        }

        String message = status == HttpStatus.INTERNAL_SERVER_ERROR ?
                "Internal server error" : ex.getMessage();

        log.error("{}: {}", message, ex.getMessage(),
                status == HttpStatus.INTERNAL_SERVER_ERROR ? ex : null);

        return ResponseEntity.status(status)
                .body(new ErrorResponse(LocalDateTime.now(), message, null));
    }
}
