package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.annotation.Nullable;

public record FilterVacancyDto(
         @Nullable
         TeamRole position,
         @Nullable
         String name
) {
}
