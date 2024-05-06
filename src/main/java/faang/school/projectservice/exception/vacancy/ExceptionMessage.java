package faang.school.projectservice.exception.vacancy;

import lombok.Getter;

@Getter
public enum ExceptionMessage {

    VACANCY_DTO_IS_NULL("Vacancy can't be null"),
    PROJECT_ID_IS_EMPTY("Project id must exist"),
    INAPPROPRIATE_ROLE("Inappropriate role"),
    NAME_IS_BLANK("Name must exist");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
