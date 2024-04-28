package faang.school.projectservice.validation.project;

import lombok.Getter;

@Getter
public enum ProjectConstraints {
    PROJECT_NAME_CAN_NOT_BE_NULL("Project name can't be null"),
    PROJECT_NAME_CAN_NOT_BE_BLANK("Project name can't be blank"),
    PROJECT_DESCRIPTION_CAN_NOT_BE_NULL("Project description can't be null"),
    PROJECT_DESCRIPTION_CAN_NOT_BE_BLANK("Project description can't be blank"),
    PROJECT_NAME_MUST_BE_UNIQUE("Project name is already exists"),
    PROJECT_NOT_EXIST("Project not found in database"),
    PROJECT_VISIBILITY_CAN_NOT_BE_NULL("Project must have visibility"),
    PROJECT_FILE_SIZE_IS_FULL("Project storage is full");
    private final String message;

    ProjectConstraints(String message) {
        this.message = message;
    }

}
