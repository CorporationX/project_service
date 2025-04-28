package faang.school.projectservice.exception.handler;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ResourceProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TeamResourceControllerExceptionHandler {

    @ExceptionHandler(ResourceProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleResourceProcessingException(ResourceProcessingException e) {
        log.error("The resource can't be processed: {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(EntityNotFoundException e) {
        log.error("Resource not found: {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e) {
        log.error("Unexpected exception caught: {}", e.getMessage(), e);
        return "An unexpected error occurred.";
    }
}
