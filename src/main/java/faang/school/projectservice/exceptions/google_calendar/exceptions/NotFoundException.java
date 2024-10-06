package faang.school.projectservice.exceptions.google_calendar.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends GoogleCalendarExceptionTemplate {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}