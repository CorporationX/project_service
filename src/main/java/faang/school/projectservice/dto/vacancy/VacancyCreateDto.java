package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VacancyCreateDto(
        @NotNull Long projectId,
        @NotBlank String description,
        @NotNull TeamRole position,
        @NotNull Integer count,
        @NotBlank String name
) {
}
