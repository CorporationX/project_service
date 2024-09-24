package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    PROJECT_CANCELED("Project canceled"),
    PROJECTS_NOT_EXIST("One of projects don't exist"),
    PROJECT_COMPLETED("Project completed"),
    PROJECT_STATUS_INVALID("Project status invalid");

    private final String message;
}
