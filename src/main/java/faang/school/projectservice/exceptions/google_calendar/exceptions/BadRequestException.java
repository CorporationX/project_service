package faang.school.projectservice.exceptions.google_calendar.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends GoogleCalendarExceptionTemplate {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
