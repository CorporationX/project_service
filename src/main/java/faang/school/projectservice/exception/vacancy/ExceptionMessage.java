package faang.school.projectservice.exception.vacancy;

import lombok.Getter;

@Getter
public enum ExceptionMessage {

    VACANCY_DTO_IS_NULL("Vacancy can't be null"),
    VACANCY_DTO_NAME_IS_BLANK("Name must exist"),
    VACANCY_DTO_NAME_IS_NULL("Name cant be null"),
    PROJECT_ID_IS_EMPTY("Project id must exist"),
    CREATOR_ID_IS_NULL("Creator id cant be null"),
    INAPPROPRIATE_ROLE("Inappropriate role");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
