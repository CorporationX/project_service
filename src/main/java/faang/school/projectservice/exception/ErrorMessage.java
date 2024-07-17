package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {

    PROJECT_CANCELED("Project canceled. You can't create stage in canceled project."),
    PROJECT_COMPLETED("Project completed. You can't create stage in completed project."),
    NULL_ID("Receiving ID is NULL. Enter correct ID.");

    private final String message;
}
