package faang.school.projectservice.exception.handler;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.BlankFieldException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ExternalServiceConnectException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.NotImageException;
import faang.school.projectservice.exception.ResizeException;
import faang.school.projectservice.exception.ResourceNotReceivedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static faang.school.projectservice.utils.Utils.stringFormatting;

/**
 * Глобальный обработчик исключений для всех REST-контроллеров.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponseMap handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> new ArrayList<>(List.of(Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid"))),
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }));
        ErrorResponseMap err = new ErrorResponseMap("validation failed", errors);
        log.warn("validation failed {}", err, ex);
        return err;
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ErrorResponseMap handleValidation(ConstraintViolationException ex) {
        Map<String, List<String>> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(v -> v.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())));
        ErrorResponseMap err = new ErrorResponseMap("validation failed", errors);
        log.warn("validation failed {}", err, ex);
        return err;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> type = ex.getRequiredType();
        String description = stringFormatting("Parameter {} must be of type {} required",
                ex.getName(),
                type != null ? type : "required");
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String description = stringFormatting(
                "A required parameter is missing {}, type: {}",
                ex.getParameterName(),
                ex.getParameterType());
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceConnectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExternalServiceConnectException(ExternalServiceConnectException ex) {
        String description = stringFormatting("no access to the service: {}", ex.getServiceName());
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String description = "Unsupported method";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    ErrorResponse handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String description = "Unsupported Content-Type";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    ErrorResponse handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String description = "Unsupported Accept";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        String description = "data_validation_error";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler({
            EmptyResultDataAccessException.class,
            EntityNotFoundException.class,
            NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(Exception ex) {
        String description = "Entity not found";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }


    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        String description = "forbidden";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        String description = "illegal_argument";
        log.warn(description, ex);
        return new ErrorResponse("illegal_argument", ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        String description = "Max upload size exceeded";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMultipartException(MultipartException ex) {
        String description = "Multipart exception";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(NotImageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotImage(NotImageException ex) {
        String description = "File is not image";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(BlankFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBlankField(BlankFieldException ex) {
        String description = "Field is blank";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex) {
        String description = "Access denied";
        log.warn(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileException(FileException ex) {
        String description = "File exception";
        log.warn(description, ex);
        return new ErrorResponse("File exception", ex.getMessage());
    }

    @ExceptionHandler(ResizeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleResizeException(ResizeException ex) {
        String description = "Error resizing image";
        log.warn(description, ex);
        return new ErrorResponse("Error resizing image", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotReceivedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleResourceNotReceived(ResourceNotReceivedException ex) {
        String description = "Error resizing image";
        log.warn(description, ex);
        return new ErrorResponse("Remote resource not received", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        String description = "Runtime exception";
        log.error(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        String description = "internal_server_error";
        log.error(description, ex);
        return new ErrorResponse(description, ex.getMessage());
    }
}