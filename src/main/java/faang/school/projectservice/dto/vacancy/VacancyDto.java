package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (объект передачи данных), представляющий вакансию в проекте.
 * <p>
 * Содержит информацию о вакансии, включая описание, роль, зарплату, список кандидатов
 * и дополнительные параметры, такие как требуемые навыки и график работы.
 *
 * @param id               Уникальный идентификатор вакансии.
 * @param name             Название вакансии.
 * @param description      Подробное описание вакансии.
 * @param position         Роль в команде, требуемая для этой вакансии (например, DEVELOPER, DESIGNER и т.д.).
 * @param projectId        Идентификатор проекта, к которому относится вакансия.
 * @param createdAt         Дата и время создания вакансии.
 * @param updatedAt         Дата и время последнего обновления вакансии.
 * @param candidates       Список кандидатов, откликнувшихся на вакансию.
 * @param status           Статус вакансии (например, OPEN, CLOSE и т.д.).
 * @param salary           Предлагаемая заработная плата.
 * @param count            Количество открытых позиций по данной вакансии.
 * @param workSchedule     Тип графика работы (например, FULL_TIME, REMOTE и т.д.).
 * @param requiredSkillIds Список идентификаторов требуемых навыков.
 * @param coverImageKey    Ключ изображения-обложки вакансии (например, путь в хранилище).
 * @author Myrza
 * @since 20.07.2025
 */
public record VacancyDto(
        Long id,
        String name,
        String description,
        TeamRole position,
        Long projectId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CandidateDto> candidates,
        VacancyStatus status,
        Double salary,
        Integer count,
        WorkSchedule workSchedule,
        List<Long> requiredSkillIds,
        String coverImageKey
) {
}
