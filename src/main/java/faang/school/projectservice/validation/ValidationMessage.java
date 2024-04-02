package faang.school.projectservice.validation;

import lombok.Getter;

@Getter
public enum ValidationMessage {
    PROJECT_NOT_NAME("Не указано название проекта"),
    VACANCY_NOT_TUTOR("Ответственный не назначен"),
    VACANCY_TUTOR_NOT_ROLE("Данный сотрудник не является куратором"),
    VACANCY_NOT_FULL("Вакансия не может быть закрыта, нужное количество не набрано"),
    TEAM_MEMBER_NOT_EVERYONE_HAS_ROLE("Вакансия не может быть закрыта, пока вся команда не получит свои роли");
    private final String message;

    ValidationMessage(String message) {
        this.message = message;
    }

}
