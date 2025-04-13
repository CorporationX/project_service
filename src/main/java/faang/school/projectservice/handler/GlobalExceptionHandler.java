package faang.school.projectservice.handler;

import faang.school.projectservice.exception.FileSizeLimitException;
import faang.school.projectservice.exception.TeamNotFoundException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.exception.UnsupportedFileTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(TeamNotFoundException e) {
        return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, e.getMessage())
                .title("Team not found")
                .detail("The specified team could not be located.")
                .property("service", "project")
                .build();
    }

    @ExceptionHandler(FileSizeLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLimitSizeFileException(FileSizeLimitException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("File size exceeded")
                .detail("The uploaded file exceeds the maximum allowed size.")
                .property("service", "project")
                .build();
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorResponse handleUnsupportedFileTypeException(UnsupportedFileTypeException e) {
        return ErrorResponse.builder(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage())
                .title("Unsupported file type")
                .detail("The uploaded file type is not supported.")
                .property("service", "project")
                .build();
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return ErrorResponse.builder(e, HttpStatus.UNAUTHORIZED, e.getMessage())
                .title("Unauthorized access")
                .detail("You are not authorized to perform this action.")
                .property("service", "project")
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())
                .title("Internal server error")
                .detail("An unexpected error occurred. Please try again later.")
                .property("service", "project")
                .build();
    }
}
