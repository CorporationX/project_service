package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record VacancyDto(
        Long id,
        String name,
        String description,
        TeamRole position,
        Integer count,
        VacancyStatus status,
        List<CandidateDto> candidates
) {
}
