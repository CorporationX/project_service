package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO для частичного обновления информации о вакансии.
 * <p>
 * Все поля являются необязательными. Только те поля, которые заданы (не null),
 * будут использованы для обновления существующей вакансии.
 *
 * @param name             Новое название вакансии. Не более 255 символов.
 * @param description      Новое описание вакансии.
 * @param position         Новая роль в команде.
 * @param status           Статус вакансии (например, OPEN, CLOSE и т.д.).
 * @param count            Обновлённое количество открытых позиций.
 * @param salary           Обновлённая предлагаемая зарплата.
 * @param workSchedule     Новый график работы.
 * @param requiredSkillIds Обновлённый список идентификаторов требуемых навыков.
 * @param coverImageKey    Новый ключ изображения-обложки вакансии (например, путь или идентификатор в хранилище).
 *                         Не более 255 символов.
 * @author Myrza
 * @since 20.07.2025
 */
public record VacancyUpdateDto(
        @Size(max = 255)
        String name,
        String description,
        TeamRole position,
        VacancyStatus status,
        @Positive
        Integer count,
        @Positive
        Double salary,
        WorkSchedule workSchedule,
        List<Long> requiredSkillIds,
        @Size(max = 255)
        String coverImageKey
) {
}
