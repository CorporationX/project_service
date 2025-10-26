package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;

import java.util.List;

public record VacancyDto(
        Long id,
        String description,
        TeamRole position,
        Integer count,
        VacancyStatus status,
        List<CandidateDto> candidates
) {
}
