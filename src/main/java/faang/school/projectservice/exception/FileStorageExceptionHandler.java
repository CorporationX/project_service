package faang.school.projectservice.exception;

import faang.school.projectservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class FileStorageExceptionHandler {

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorageException(
            FileStorageException e) {
        log.error("File storage error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .errorCode("FILE_STORAGE_ERROR")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleStorageLimitExceeded(
            StorageLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE)
                .body(ErrorResponse.builder()
                        .errorCode("STORAGE_LIMIT_EXCEEDED")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .errorCode("RESOURCE_NOT_FOUND")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .errorCode("ACCESS_DENIED")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(
            MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ErrorResponse.builder()
                        .errorCode("FILE_TOO_LARGE")
                        .message("File size exceeds maximum allowed size")
                        .build());
    }
}