package faang.school.projectservice.handler;

import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class InternshipExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostAlreadyPublishedException(DataValidateException e) {
        log.error("DataValidateException.", e);
        return new ErrorResponse(e.getMessage());
    }

}
