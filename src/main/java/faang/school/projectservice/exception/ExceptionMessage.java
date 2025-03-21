package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    PROJECT_NOT_FOUND("Project with id=%d not found"),
    PERMISSION_DENIED("Only a team member with the MANAGER role or the project owner can create fundraising activities for a project");

    private final String message;
    
    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
