package faang.school.projectservice.exceptions.google_calendar.exceptions;

import org.springframework.http.HttpStatus;

public class GoogleCalendarException extends GoogleCalendarExceptionTemplate {
    public GoogleCalendarException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public GoogleCalendarException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
