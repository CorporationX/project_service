package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO для создания новой вакансии.
 * <p>
 * Содержит данные, необходимые для создания вакансии в рамках проекта, включая
 * название, описание, требуемую роль, зарплату, график работы и список необходимых навыков.
 * </p>
 *
 * @param name             Название вакансии. Обязательное поле, не более 255 символов.
 * @param description      Описание вакансии (может быть пустым).
 * @param position         Роль в команде, которую необходимо закрыть. Обязательное поле.
 * @param projectId        Идентификатор проекта, к которому относится вакансия. Обязательное поле.
 * @param count            Количество открытых позиций по вакансии. Обязательное поле.
 * @param salary           Предлагаемая заработная плата (может быть пустым).
 * @param workSchedule     График работы (например, FULL_TIME, REMOTE и т.д.). Обязательное поле.
 * @param requiredSkillIds Список идентификаторов навыков, необходимых для кандидата. Обязательное поле.
 * @param coverImageKey    Ключ изображения-обложки вакансии (например, путь или идентификатор в хранилище).
 *                         Не более 255 символов.
 * @author Myrza
 * @since 20.07.2025
 */
public record VacancyCreateDto(
        @NotBlank
        @Size(max = 255)
        String name,
        String description,
        @Enumerated(EnumType.STRING)
        @NotNull
        TeamRole position,
        @NotNull
        Long projectId,
        @NotNull
        @Positive
        Integer count,
        @Positive
        Double salary,
        @Enumerated(EnumType.STRING)
        @NotNull
        WorkSchedule workSchedule,
        @NotNull
        List<Long> requiredSkillIds,
        @Size(max = 255)
        String coverImageKey
) {
}
