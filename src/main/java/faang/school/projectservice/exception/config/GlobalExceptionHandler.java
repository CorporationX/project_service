package faang.school.projectservice.exception.config;

import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileException(FileException e) {
        log.error("File exception occured: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
    public ErrorResponse handleStorageException(FileException e) {
        log.error("Storage exception occured: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage());
    }
}
