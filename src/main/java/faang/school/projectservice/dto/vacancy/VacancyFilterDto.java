package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

@Builder
public record VacancyFilterDto(
        TeamRole position,
        String nameContains) {}
