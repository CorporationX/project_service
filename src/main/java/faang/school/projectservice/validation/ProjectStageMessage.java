package faang.school.projectservice.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectStageMessage {
    PROJECT_STAGE_NOT_FOUND("Проект не был найден"),
    PROJECT_INCORRECT_STATUS("Нельзя создать этап для проекта с данным статусом"),
    STAGE_NOT_FOUND("Этап не был найден");

    private final String message;
}
