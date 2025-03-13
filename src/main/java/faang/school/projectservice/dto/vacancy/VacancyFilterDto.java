package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;

public record VacancyFilterDto(
        String namePattern,
        TeamRole positionPattern) {
}
