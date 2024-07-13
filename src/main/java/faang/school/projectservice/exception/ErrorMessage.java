package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    MOMENT_PROJECTS_AND_MEMBERS_NULL("Both fields (projectIds, teamMemberIds) can not be null"),
    SOME_OF_MEMBERS_NOT_EXIST("Some of passed team members don't exist"),
    SOME_OF_PROJECTS_NOT_EXIST("Some of passed projects don't exist"),
    PROJECT_STATUS_INVALID("Some status of passed projects invalid"),
    MEMBERS_UNFIT_PROJECTS("Some of passed team members don't participate passed projects"),
    ;

    private final String message;
}
