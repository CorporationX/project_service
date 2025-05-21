package faang.school.projectservice.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long teamId) {
        super("Team with id=%d not found".formatted(teamId));
    }
}
