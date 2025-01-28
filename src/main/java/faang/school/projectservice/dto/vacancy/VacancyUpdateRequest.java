package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VacancyUpdateRequest(
        @NotNull
        Long id,

        @NotNull
        String position,

        @Min(1)
        int availableSlots
) {
}
