package faang.school.projectservice.validate;

import faang.school.projectservice.exception.FileSizeLimitException;
import faang.school.projectservice.exception.TeamNotFoundException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.exception.UnsupportedFileTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AvatarRequestValidator {
    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(TeamNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(FileSizeLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLimitSizeFileException(FileSizeLimitException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorResponse handleUnsupportedFileTypeException(UnsupportedFileTypeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
