package faang.school.projectservice.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.DatabaseCorruptedException;
import faang.school.projectservice.exception.ErrorResponse;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.RecordNotFoundException;
import faang.school.projectservice.exception.ResourceForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DatabaseCorruptedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDatabaseCorruptedException(DatabaseCorruptedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileException(FileException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRecordNotFoundException(RecordNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleResourceForbiddenException (ResourceForbiddenException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMultipartException() {
        return new ErrorResponse("You haven't passed an image. " +
                "Or the Content-Type is invalid (multipart/form-data)");
    }

    @ExceptionHandler(AmazonS3Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAmazonS3Exception(AmazonS3Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException (Exception e) {
        return new ErrorResponse(e.getMessage());
    }
}
