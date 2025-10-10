package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;

import java.time.LocalDateTime;

public record VacancyDto(
        String name,
        String description,
        TeamRole position,
        Integer count,
        Long projectId,
        VacancyStatus status,
        LocalDateTime createdAt
) {

}
