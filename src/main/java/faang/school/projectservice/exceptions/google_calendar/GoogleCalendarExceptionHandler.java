package faang.school.projectservice.exceptions.google_calendar;

import faang.school.projectservice.exceptions.google_calendar.exceptions.BadRequestException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.TokenCreatedException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GoogleCalendarExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error("Ресурс не найден: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        log.error("Некорректный запрос: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(GoogleCalendarException.class)
    public ResponseEntity<String> handleGoogleCalendarException(GoogleCalendarException ex) {
        log.error("Ошибка Google Calendar: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка Google Calendar: " + ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Ошибка авторизации: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка авторизации: " + ex.getMessage());
    }
    @ExceptionHandler(TokenCreatedException.class)
    public ResponseEntity<String> handleTokenCreatedException(TokenCreatedException ex) {
        log.error("Ошибка при работе с токеном: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при работе с токеном: " + ex.getMessage());
    }
}