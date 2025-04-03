package faang.school.projectservice.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    FILE_UPLOAD_EXCEPTION("Failed to upload file: %s"),
    ACCESS_DENIED_FOR_PROJECT("User is not a member of the project (projectId = %d, userId = %d)"),
    STORAGE_LIMIT_EXCEEDED("Storage limit exceeded for project (projectId = %d). Limit: %s bytes, Attempted: %s bytes"),
    PROJECT_NOT_FOUND("Project with id = %d not found"),
    RESOURCE_NOT_FOUND("Resource with id = %d not found"),
    RESOURCE_DOES_NOT_BELONG_TO_PROJECT("Resource id = %d does not belong to project id = %d"),
    RESOURCE_DELETE_FORBIDDEN("User id = %d is not allowed to delete resource id = %d");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
