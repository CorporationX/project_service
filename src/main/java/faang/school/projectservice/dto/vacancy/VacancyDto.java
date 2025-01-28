package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;

public record VacancyDto(
        Long id,
        TeamRole position,
        int availableSlots,
        String curator,
        String status
) {
}
