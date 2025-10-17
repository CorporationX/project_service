package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateVacancyDto(
    String name,
    String description,
    @NotNull
    long projectId,
    VacancyStatus status,
    @NotNull
    TeamRole position,
    @NotNull
    int count,
    WorkSchedule workSchedule
) {
}
