package faang.school.projectservice.dto;

import java.time.LocalDateTime;

public record VacancyDto(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy,
        String coverImageKey
) {
}
