package faang.school.projectservice.exception;

import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.presentation.FileDownloadException;
import faang.school.projectservice.exception.presentation.FileUploadException;
import faang.school.projectservice.exception.presentation.ProjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProjectNotFoundException(ProjectNotFoundException e) {
        return new ErrorResponse(
                "Project Error",
                "The project you are looking for was not found.",
                e.getMessage());
    }

    @ExceptionHandler(FileDownloadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDownloadFileFromMinioException(FileDownloadException e) {
        return new ErrorResponse(
                "Download Error",
                "Failed to download file from storage service.",
                e.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUploadFileToMinioError(FileUploadException e) {
        return new ErrorResponse(
                "Upload Error",
                "Failed to upload file to storage service.",
                e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptionError(Exception e) {
        return new ErrorResponse(
                "Error",
                "Failed.",
                e.getMessage());
    }
}
