package faang.school.projectservice.exceptions.google_calendar;

import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarExceptionTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GoogleCalendarExceptionHandler {
    @ExceptionHandler(GoogleCalendarExceptionTemplate.class)
    public ResponseEntity<String> handleApiException(GoogleCalendarExceptionTemplate ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("I/O exception: " + ex.getMessage());
    }
}