package faang.school.projectservice.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @Nullable
        List<Long> candidatesIds
) {

}
