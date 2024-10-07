package faang.school.projectservice.exception;

public class UserNotTeamMemberException extends RuntimeException {
    public UserNotTeamMemberException(String message) {
        super(message);
    }
}
