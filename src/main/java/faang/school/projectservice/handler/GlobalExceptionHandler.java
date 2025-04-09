package faang.school.projectservice.handler;

import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.ProjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException e) {
       log.error("Project is not found", e);
       ErrorResponse errorResponse = new ErrorResponse(
               e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
