package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import org.jetbrains.annotations.NotNull;

public record VacancyCreationRequest(
        @NotNull
        TeamRole position,      // Обязательное поле
        @Min(1)
        int availableSlots,
        @NotNull
        String curatorUsername
) {
}