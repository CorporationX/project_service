package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessages {
    PROJECT_NOT_FOUND("Project with id %d not found"),
    WRONG_PROJECT_STATUS("Project status can't be cancelled or completed. Project id %d"),
    STAGE_NOT_FOUND("Stage with id %d not found"),
    MIGRATE_STAGE_ID_IS_REQUIRED("Stage id for migrate can't be null"),
    EXECUTOR_NOT_FOUNT("Team executor with id %d not found");

    private final String message;
}
