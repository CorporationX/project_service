package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record OpenVacancyRequestDto(
        @NotNull(message = "Vacancy name is required")
        @NotBlank(message = "Vacancy name cannot be blank")
        String name,

        @NotNull(message = "Vacancy description is required")
        @NotBlank(message = "Vacancy description cannot be blank")
        String description,

        long projectId,

        @NotNull(message = "Position is required")
        TeamRole position,

        @Min(value = 1, message = "A vacancy cannot be opened without candidates")
        int requiredCandidatesCount,

        long authorId,

        @Nullable
        Double salary,

        @Nullable
        WorkSchedule workSchedule,
        @Nullable
        String coverImageKey) {
}
