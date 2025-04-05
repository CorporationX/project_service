package faang.school.projectservice.exception;

public class TeamNotFoundException extends IllegalArgumentException {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
