package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;

import java.util.List;

public record VacancyDto(
        Long id,
        String name,
        String description,
        Long projectId,
        VacancyStatus status,
        TeamRole position,
        int count,
        WorkSchedule workSchedule,
        List<CandidateDto> candidates
) {

}
