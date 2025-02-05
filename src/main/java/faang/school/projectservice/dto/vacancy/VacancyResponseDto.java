package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Builder;

import java.util.List;

@Builder
public record VacancyResponseDto(
        String name,
        String description,
        TeamRole position,
        Long projectId,
        List<Long> candidatesId,
        VacancyStatus status,
        Double salary,
        WorkSchedule workSchedule,
        Integer count,
        List<Long> requiredSkillIds
) {

}
