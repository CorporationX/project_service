package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VacancyCreateDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        Long projectId,
        @NotNull
        TeamRole position,
        @NotNull
        Integer count
) {

}
