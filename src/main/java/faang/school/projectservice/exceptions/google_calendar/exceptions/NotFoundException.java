package faang.school.projectservice.exceptions.google_calendar.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}