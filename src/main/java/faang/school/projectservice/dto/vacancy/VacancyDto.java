package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;

public record VacancyDto(@NotNull long id, @NotNull TeamRole position, @NotNull int count, @NotNull long creatorId) {
}
