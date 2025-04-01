package faang.school.projectservice.handler.presentation;

import faang.school.projectservice.exception.presentation.FileDownloadException;
import faang.school.projectservice.exception.presentation.ProjectNotFoundException;
import faang.school.projectservice.exception.presentation.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class PresentationExceptionController {
    private final ErrorMessageService errorMessageService;

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException e) {
        ErrorResponse error = errorMessageService.buildErrorResponse(
                "Project Error",
                "The project you are looking for was not found.",
                e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<ErrorResponse> handleDownloadFileFromMinioException(FileDownloadException e) {
        ErrorResponse error = errorMessageService.buildErrorResponse(
                "Download Error",
                "Failed to download file from storage service.",
                e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleUploadFileToMinioError(FileUploadException e) {
        ErrorResponse error = errorMessageService.buildErrorResponse(
                "Upload Error",
                "Failed to upload file to storage service.",
                e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
