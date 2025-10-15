package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Builder;

@Builder
public record CreateVacancyDto(
    String name,
    String description,
    long projectId,
    VacancyStatus status,
    TeamRole position,
    int count,
    WorkSchedule workSchedule
) {
}
