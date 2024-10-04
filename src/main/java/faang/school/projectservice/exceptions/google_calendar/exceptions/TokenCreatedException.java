package faang.school.projectservice.exceptions.google_calendar.exceptions;

import org.springframework.http.HttpStatus;

public class TokenCreatedException extends GoogleCalendarExceptionTemplate {
    public TokenCreatedException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
