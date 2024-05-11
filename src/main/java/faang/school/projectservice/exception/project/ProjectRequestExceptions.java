package faang.school.projectservice.exception.project;

import lombok.Getter;

@Getter
public enum ProjectRequestExceptions {
    OWNER_ID_EMPTY("Project request owner ID is empty"),
    NAME_EMPTY("Project request name is empty"),
    DESCRIPTION_EMPTY("Project description name is empty"),
    ALREADY_EXISTS("Project with this name and owner id already exists"),
    STORAGE_SIZE_INVALID("Project with this name and owner id already exists"),
    STATUS_IMMUTABLE("Project status can't be changed");

    private final String message;

    ProjectRequestExceptions(String message) {
        this.message = message;
    }
}
