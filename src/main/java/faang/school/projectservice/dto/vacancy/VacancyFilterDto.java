package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

@Builder
public record VacancyFilterDto(
        String nameContains,
        TeamRole position
) {}