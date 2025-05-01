package faang.school.projectservice.handler;

import faang.school.projectservice.exception.FileSizeLimitException;
import faang.school.projectservice.exception.TeamNotFoundException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.exception.UnsupportedFileTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(TeamNotFoundException e) {
        ErrorResponse error =  ErrorResponse.builder(e, HttpStatus.NOT_FOUND, e.getMessage())
                .title("Team not found")
                .detail("The specified team could not be located.")
                .property("service", "project")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(FileSizeLimitException.class)
    public ResponseEntity<ErrorResponse> handleLimitSizeFileException(FileSizeLimitException e) {
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("File size exceeded")
                .detail("The uploaded file exceeds the maximum allowed size.")
                .property("service", "project")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedFileTypeException(UnsupportedFileTypeException e) {
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage())
                .title("Unsupported file type")
                .detail("The uploaded file type is not supported.")
                .property("service", "project")
                .build();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        ErrorResponse error =  ErrorResponse.builder(e, HttpStatus.UNAUTHORIZED, e.getMessage())
                .title("Unauthorized access")
                .detail("You are not authorized to perform this action.")
                .property("service", "project")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse error =  ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())
                .title("Internal server error")
                .detail("An unexpected error occurred. Please try again later.")
                .property("service", "project")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
