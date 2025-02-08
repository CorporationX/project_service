package faang.school.projectservice.exception;

public class UserIsNotInThatProjectException extends RuntimeException {
    public UserIsNotInThatProjectException(String message) {
        super(message);
    }
}
