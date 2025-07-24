package faang.school.projectservice.exception.handler;

import faang.school.projectservice.dto.error.ErrorResponse;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.BlankFieldException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.NotImageException;
import faang.school.projectservice.exception.ResizeException;
import faang.school.projectservice.exception.ResourceNotReceivedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Max upload size exceeded", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MultipartException.class)
    public ErrorResponse handleMultipartException(MultipartException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Multipart exception", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotImageException.class)
    public ErrorResponse handleNotImage(NotImageException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("File is not image", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BlankFieldException.class)
    public ErrorResponse handleBlankField(BlankFieldException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Field is blank", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Entity not found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDenied(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Access denied", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FileException.class)
    public ErrorResponse handleFileException(FileException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("File exception", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ResizeException.class)
    public ErrorResponse handleResizeException(ResizeException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Error resizing image", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ResourceNotReceivedException.class)
    public ErrorResponse handleResourceNotReceived(ResourceNotReceivedException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Remote resource not received", e.getMessage());
    }
}
