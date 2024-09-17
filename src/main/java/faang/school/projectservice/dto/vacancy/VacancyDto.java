package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VacancyDto(
        Long id,
        @NotBlank(message = "Name must not be blank")
        String name,
        @NotBlank(message = "Description must not be blank")
        String description,
        @NotNull(message = "Project ID must not be null")
        Long projectId,
        List<Long> candidateIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @NotNull
        Long createdBy,
        Long updatedBy,
        @NotBlank(message = "Status must not be blank")
        String status,
        @PositiveOrZero(message = "Salary must not be negative")
        Double salary,
        @NotBlank(message = "Work schedule must not be blank")
        String workSchedule,
        @Positive(message = "Vacancy must have at least one position")
        Integer count,
        @NotEmpty(message = "The list of required skill IDs must not be empty")
        List<Long> requiredSkillIds
) {
}