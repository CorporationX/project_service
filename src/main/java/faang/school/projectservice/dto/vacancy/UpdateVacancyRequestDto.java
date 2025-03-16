package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;

public record UpdateVacancyRequestDto(
        @NotNull(message = "Vacancy name is required")
        @NotBlank(message = "Vacancy name cannot be blank")
        String name,
        @Nullable String description,
        long projectId,
        @Nullable TeamRole position,
        @Min(value = 1, message = "A vacancy cannot be opened without candidates")
        @Nullable
        Long requiredCandidatesCount,
        @Nullable List<CandidateDto> candidates,
        @Nullable Double salary,
        @Nullable WorkSchedule workSchedule,
        @Nullable String coverImageKey) {
}
