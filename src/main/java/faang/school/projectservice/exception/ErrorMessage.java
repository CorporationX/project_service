package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {

    PROJECT_CANCELED("Project canceled. You can't create stage in canceled project."),
    PROJECT_COMPLETED("Project completed. You can't create stage in completed project.");
    //TEAM_MEMBER_WITHOUT_ROLE("There is a team member without role in the list of stage executors.");

    private final String message;
}
