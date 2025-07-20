package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для фильтрации вакансий по различным критериям.
 * <p>
 * Позволяет производить поиск вакансий по названию, роли, графику работы,
 * диапазону зарплаты, количеству позиций, требуемым навыкам и интервалам дат создания/обновления.
 *
 * @param nameContains     Название вакансии (поиск по подстроке).
 * @param position         Роль в команде (TeamRole), по которой производится фильтрация.
 * @param workSchedule     Тип графика работы (например, ПОЛНЫЙ_ДЕНЬ, УДАЛЁННО и т.д.).
 * @param status           Статус вакансии (например, OPEN, CLOSE и т.д.).
 * @param minSalary        Минимальная зарплата (включительно).
 * @param maxSalary        Максимальная зарплата (включительно).
 * @param minCount         Минимальное количество открытых позиций.
 * @param maxCount         Максимальное количество открытых позиций.
 * @param requiredSkillIds Список идентификаторов навыков, которые должны присутствовать у кандидата.
 * @param createdAtFrom    Начало диапазона даты создания вакансии (включительно).
 * @param createdAtTo      Конец диапазона даты создания вакансии (включительно).
 * @param updatedAtFrom    Начало диапазона даты последнего обновления вакансии (включительно).
 * @param updatedAtTo      Конец диапазона даты последнего обновления вакансии (включительно).
 * @author Myrza
 * @since 20.07.2025
 */
public record VacancyFilterDto(
        @Size(max = 255)
        String nameContains,
        TeamRole position,
        WorkSchedule workSchedule,
        VacancyStatus status,
        @Positive
        Double minSalary,
        @Positive
        Double maxSalary,
        @Positive
        Integer minCount,
        @Positive
        Integer maxCount,
        List<Long> requiredSkillIds,
        @PastOrPresent
        LocalDateTime createdAtFrom,
        @PastOrPresent
        LocalDateTime createdAtTo,
        @PastOrPresent
        LocalDateTime updatedAtFrom,
        @PastOrPresent
        LocalDateTime updatedAtTo
) {
    public void validate() {
        if (minCount != null && maxCount != null && minCount > maxCount) {
            throw new DataValidationException("Минимальное количество вакансии не может быть больше максимальной");
        }

        if (createdAtFrom != null && createdAtTo != null && createdAtFrom.isAfter(createdAtTo)) {
            throw new DataValidationException("Диапазон даты создания задан не корректно");
        }

        if (updatedAtFrom != null && updatedAtTo != null & updatedAtFrom.isAfter(updatedAtTo)) {
            throw new DataValidationException("Диапазон даты обновления задан не корректно");
        }
    }
}
