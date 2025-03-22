package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    PROJECT_NOT_FOUND("Project with id=%d not found"),
    PERMISSION_DENIED("Only a team member with the MANAGER role or the project owner can create fundraising activities for a project"),
    CAMPAIGN_NOT_FOUND("Campaign with id=%d not found"),
    CAMPAIGN_CREATOR_MODIFICATION("Unable to change campaign creator"),
    DATE_PARSE("Invalid date format: %s. Expected format: yyyy-MM-dd");

    private final String message;
    
    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
