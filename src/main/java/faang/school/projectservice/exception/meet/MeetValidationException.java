package faang.school.projectservice.exception.meet;

public class MeetValidationException extends RuntimeException {
    public MeetValidationException(String format, Object... args) {
        super(String.format(format, args));
    }
}
