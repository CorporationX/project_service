package faang.school.projectservice.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(String.format("Event not found: %s", message));
    }
}
