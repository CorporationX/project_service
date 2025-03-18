package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import org.springframework.lang.Nullable;

public record UpdateVacancyRequestDto(
        long vacancyId,
        long teamMemberUpdaterId,
        @Nullable String name,
        @Nullable String description,
        @Nullable TeamRole position,
        @Nullable VacancyStatus status,
        @Min(value = 1, message = "A vacancy cannot be opened without candidates")
        @Nullable
        Integer requiredCandidatesCount,
        @Nullable Double salary,
        @Nullable WorkSchedule workSchedule,
        @Nullable String coverImageKey) {
}
