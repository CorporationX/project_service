package faang.school.projectservice.exception;

import faang.school.projectservice.model.dto.error.ErrorResponse;
import faang.school.projectservice.exception.subproject.SubProjectNotFinishedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalEntityException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalEntityException(RuntimeException exception) {
        log.error("Exception occurred: ", exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundException(EntityNotFoundException exception) {
        log.error("Entity not found occurred: ", exception);
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(SubProjectNotFinishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse subProjectNotFinishedException(SubProjectNotFinishedException exception) {
        log.error("SubProject not finished occurred: ", exception);
        return new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileException(FileException exception) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException occurred: ", exception);
        return exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(JiraException.class)
    public ErrorResponse handleJiraErrorResponseException(JiraException ex) {
        log.error("{}, {}", ex.getStatus(), ex.getMessage());
        return new ErrorResponse(ex.getStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    public ErrorResponse dataValidationException(DataValidationException exception){
        log.error("Data validation exception occurred: ", exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
