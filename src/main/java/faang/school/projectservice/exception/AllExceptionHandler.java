package faang.school.projectservice.exception;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static faang.school.projectservice.constants.Constants.BUCKET_FAIL;
import static faang.school.projectservice.constants.Constants.NO_PERMISSION;
import static faang.school.projectservice.constants.Constants.PROJECT_NOT_FOUND;
import static faang.school.projectservice.constants.Constants.RESOURCE_NOT_FOUND;
import static faang.school.projectservice.constants.Constants.STORAGE_LIMIT_EXCEEDED;
import static faang.school.projectservice.constants.Constants.UPLOAD_FAIL;
import static faang.school.projectservice.constants.Constants.USER_NOT_FOUND;

@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class AllExceptionHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProjectNotFoundException(ProjectNotFoundException ex) {
        log.error("Project not found: {}", ex.getMessage());
        return ErrorResponse.builder().report(PROJECT_NOT_FOUND).message(ex.getMessage()).build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ErrorResponse.builder().report(RESOURCE_NOT_FOUND).message(ex.getMessage()).build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ErrorResponse.builder().report(USER_NOT_FOUND).message(ex.getMessage()).build();
    }

    @ExceptionHandler(AccessToDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(AccessToDeniedException ex) {
        log.error("Forbidden access: {}", ex.getMessage());
        return ErrorResponse.builder().report(NO_PERMISSION).message(ex.getMessage()).build();
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStorageLimitExceeded(StorageLimitExceededException ex) {
        log.error("Storage limit exceeded: {}", ex.getMessage());
        return ErrorResponse.builder().report(STORAGE_LIMIT_EXCEEDED).message(ex.getMessage()).build();
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileStorageException(FileStorageException ex) {
        log.error("File storage error: {}", ex.getMessage(), ex);
        return ErrorResponse.builder().report(UPLOAD_FAIL).message(ex.getMessage()).build();
    }

    @ExceptionHandler(BucketCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleBucketCreationException(BucketCreationException ex) {
        log.error("Bucket creation error: {}", ex.getMessage());
        return ErrorResponse.builder().report(BUCKET_FAIL).message(ex.getMessage()).build();
    }
}
