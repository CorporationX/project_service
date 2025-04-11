package faang.school.projectservice.exception;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
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
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ProblemDetail handleProjectNotFoundException(ProjectNotFoundException ex) {
        log.error("Project not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(NOT_FOUND);
        problemDetail.setTitle("Project not found");
        problemDetail.setDetail(PROJECT_NOT_FOUND);
        problemDetail.setProperty("report", "PROJECT_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(NOT_FOUND);
        problemDetail.setTitle("Resource not found");
        problemDetail.setDetail(RESOURCE_NOT_FOUND);
        problemDetail.setProperty("report", "RESOURCE_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(NOT_FOUND);
        problemDetail.setTitle("User not found");
        problemDetail.setDetail(USER_NOT_FOUND);
        problemDetail.setProperty("report", "USER_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(AccessToDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ProblemDetail handleForbiddenException(AccessToDeniedException ex) {
        log.error("Forbidden access: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(FORBIDDEN);
        problemDetail.setTitle("Access denied");
        problemDetail.setDetail(NO_PERMISSION);
        problemDetail.setProperty("report", "NO_PERMISSION");
        return problemDetail;
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleStorageLimitExceeded(StorageLimitExceededException ex) {
        log.error("Storage limit exceeded: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setTitle("Storage limit exceeded");
        problemDetail.setDetail(STORAGE_LIMIT_EXCEEDED);
        problemDetail.setProperty("report", "STORAGE_LIMIT_EXCEEDED");
        return problemDetail;
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleFileStorageException(FileStorageException ex) {
        log.error("File storage error: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setTitle("File storage error");
        problemDetail.setDetail(UPLOAD_FAIL);
        problemDetail.setProperty("report", "UPLOAD_FAIL");
        return problemDetail;
    }

    @ExceptionHandler(BucketCreationException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleBucketCreationException(BucketCreationException ex) {
        log.error("Bucket creation error: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setTitle("Bucket creation error");
        problemDetail.setDetail(BUCKET_FAIL);
        problemDetail.setProperty("report", "BUCKET_FAIL");
        return problemDetail;
    }
}
