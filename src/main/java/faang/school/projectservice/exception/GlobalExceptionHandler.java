package faang.school.projectservice.exception;

import faang.school.projectservice.dto.response.ErrorResponse;
import faang.school.projectservice.dto.response.Violation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DataValidationException.class, IllegalArgumentException.class})
    public ErrorResponse handleValidationExceptions(DataValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Violation> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final List<Violation> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(new Violation(
                    (((FieldError) error).getField()), error.getDefaultMessage()
            ));
        });
        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Violation> onConstraintValidationException(ConstraintViolationException ex) {
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .toList();
        log.error(ex.getMessage(), ex);
        return violations;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenAccessException.class)
    public ErrorResponse handleForbiddenAccessException(ForbiddenAccessException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvitationAlreadyExistsException.class)
    public ErrorResponse handleInvitationAlreadyExistsException(InvitationAlreadyExistsException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidInvitationStatusException.class)
    public ErrorResponse handleInvalidInvitationStatusException(InvalidInvitationStatusException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }


    // TODO переделать и добавить логи
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<String> handleInvalidFileException(InvalidFileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
    }
}
