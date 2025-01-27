package faang.school.projectservice.handler;

import faang.school.projectservice.dto.ErrorDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorDto> handleDataValidationException(DataValidationException ex) {
        log.error(ex.getMessage());
        ErrorDto dto = new ErrorDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage());
        ErrorDto dto = new ErrorDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }
}
