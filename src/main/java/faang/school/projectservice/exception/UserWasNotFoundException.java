package faang.school.projectservice.exception;

public class UserWasNotFoundException extends RuntimeException {
    public UserWasNotFoundException(String message) {
        super(message);
    }
}
