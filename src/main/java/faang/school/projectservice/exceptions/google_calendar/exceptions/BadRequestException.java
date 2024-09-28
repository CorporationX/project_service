package faang.school.projectservice.exceptions.google_calendar.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
