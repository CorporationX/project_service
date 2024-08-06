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
    MEMBERS_UNFIT_PROJECTS("Some of passed team members are excess or need to pass their projects"),
    PROJECTS_UNFIT_MEMBERS("Some of passed projects are excess or need to pass members of these projects"),
    MOMENT_NOT_EXIST("Moment with passed ID don't exist"),
    TEAMS_NOT_FOUND("Teams with passed IDs have not been found"),
    STAGES_NOT_FOUND("Stages with passed IDs have not been found"),
    ;

    private final String message;
}
