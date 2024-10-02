package faang.school.projectservice.exceptions.google_calendar.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GoogleCalendarExceptionTemplate extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public GoogleCalendarExceptionTemplate(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public GoogleCalendarExceptionTemplate(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.message = message;
    }
}