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
    DATE_PARSE("Invalid date format: %s. Expected format: yyyy-MM-dd"),
    EMPTY_FILTER("At least one filter parameter must be provided"),
    FILE_NOT_VALID("File is not valid"),
    FILE_NOT_SENT("File is missing"),
    UNABLE_READ_FILE("Error while reading file bytes"),
    IMAGE_PROCESSING_WRITE("Image processing error while saving"),
    IMAGE_PROCESSING_READ("Image processing error while reading"),
    S3_UPLOAD("Failed to upload file to S3");


    private final String message;
    
    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
