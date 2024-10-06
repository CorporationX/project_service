package faang.school.projectservice.exceptions.google_calendar.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GoogleCalendarExceptionTemplate {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}