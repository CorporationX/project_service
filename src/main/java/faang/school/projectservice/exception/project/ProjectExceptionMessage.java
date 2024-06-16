package faang.school.projectservice.exception.project;

import lombok.Getter;

@Getter
public enum ProjectExceptionMessage {
    OWNER_ID_EMPTY("Project request owner %s is empty"),
    NAME_EMPTY("Project request name is empty"),
    DESCRIPTION_EMPTY("Project description name is empty"),
    ALREADY_EXISTS("Project with this name and owner id already exists"),
    NOT_FOUND("Project with this name and owner id not found"),
    STORAGE_SIZE_INVALID("Project with this name and owner id already exists"),
    STORAGE_SIZE_MAX_EXCEED("If file will be uploaded, it exceeds project max storage size"),
    NO_COVER("Project don't have cover"),
    STATUS_IMMUTABLE("Project status can't be changed"),
    SUBPROJECT_VISIBILITY_INVALID("Public projects can't have private subprojects"),
    SUBPROJECT_NOT_FINISHED_EXCEPTION("Project can not be finished until all its subproject will be finished");

    private final String message;

    ProjectExceptionMessage(String message) {
        this.message = message;
    }
}
