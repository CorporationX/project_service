package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.annotation.Nullable;

public record VacancyUpdateDto(
        @Nullable
        String name,
        @Nullable
        String description,
        @Nullable
        VacancyStatus vacancyStatus,
        @Nullable
        Long candidateId,
        @Nullable
        Long teamId
) {
}
