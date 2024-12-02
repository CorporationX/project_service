package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateDeleteVacancyDto(
        @NotNull @Positive Long id,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive Long projectId,
        List<Long> candidatesIds,
        @NotBlank VacancyStatus status,
        @NotNull @Positive Long createdBy,
        @NotNull Integer count
) {
}