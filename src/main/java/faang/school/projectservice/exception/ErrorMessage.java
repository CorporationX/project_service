package faang.school.projectservice.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    USER_NOT_FOUND("User is not found!"),
    VALIDATION_EXCEPTION("Validation is not passed"),
    UNACCEPTABLE_EMPTY_INTERNS_LIST("The list of interns' ids is empty"),
    UNACCEPTABLE_DATE_INTERVAL("The internship should be no longer than 3 months"),
    UNACCEPTABLE_INTERNSHIP_STATUS("The internship should be completed first"),
    INTERNSHIP_NOT_FOUND_EXCEPTION("The internship is not found"),
    FILE_EXCEPTION("An error occurred while processing the file."),
    FILE_STORAGE_CAPACITY_EXCEEDED("File storage capacity exceeded."),
    TEAM_MEMBER_BY_USER_AND_PROJECT_IDS_NOT_FOUND_EXCEPTION("TeamMember with user id %d and project id %d not found"),
    TEAM_MEMBER_NOT_FOUND("TeamMember not found"),

    FILE_NOT_PROVIDED("No file provided."),
    FILE_SIZE_EXCEEDED("File size exceeds the maximum allowed limit of 5MB."),
    INVALID_FILE_TYPE("Only image files are allowed."),
    INVALID_IMAGE_FILE("Invalid image file.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

}
