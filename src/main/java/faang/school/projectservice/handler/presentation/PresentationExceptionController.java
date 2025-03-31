package faang.school.projectservice.handler.presentation;

import faang.school.projectservice.exception.presentation.DownloadFileFromMinioException;
import faang.school.projectservice.exception.presentation.ProjectNotFoundException;
import faang.school.projectservice.exception.presentation.UploadFileToMinioError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PresentationExceptionController {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DownloadFileFromMinioException.class)
    public ResponseEntity<String> handleDownloadFileFromMinioException(DownloadFileFromMinioException e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UploadFileToMinioError.class)
    public ResponseEntity<String> handleUploadFileToMinioError(UploadFileToMinioError e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
