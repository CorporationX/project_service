package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.annotation.Nullable;


public record VacancyUpdateDto(
        @Nullable String name,
        @Nullable String description,
        @Nullable TeamRole position,
        @Nullable Integer count
) {
}
