package faang.school.projectservice.exception.handler;

import faang.school.projectservice.exception.ConstraintViolation;
import faang.school.projectservice.exception.DataAccessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.ProjectStatusException;
import faang.school.projectservice.exception.SizeExceeded;
import faang.school.projectservice.exception.TeamMemberNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolation.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolation e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileException(FileException e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ProjectStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProjectStatusException(ProjectStatusException e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(SizeExceeded.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSizeExceededException(SizeExceeded e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(TeamMemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTeamMemberNotFoundException(TeamMemberNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.NOT_FOUND, e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}