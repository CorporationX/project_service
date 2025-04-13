package faang.school.projectservice.exception;

public class MeetNotFoundException extends RuntimeException {
    public MeetNotFoundException(long id) {
        super("Meet with id " + id + " not found");
    }
}
