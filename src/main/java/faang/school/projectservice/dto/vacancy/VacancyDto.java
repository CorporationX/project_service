package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record VacancyDto(
        String name,
        String description,
        TeamRole position,
        Integer count,
        Long projectId,
        VacancyStatus status,
        LocalDateTime createdAt,
        @Nullable
        List<Long> candidatesIds
) {

}
