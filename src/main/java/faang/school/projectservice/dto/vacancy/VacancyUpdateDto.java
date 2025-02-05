package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record VacancyUpdateDto(
        Long id,

        @NotBlank(message = "name must be not empty")
        String name,

        @NotBlank(message = "description must be not empty")
        String description,

        @NotNull(message = "position must be not null")
        TeamRole position,

        @NotNull(message = "projectId must be not null")
        @Min(value = 0, message = "projectId must be minimum 0")
        Long projectId,

        Double salary,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "workSchedule must be not null")
        WorkSchedule workSchedule,

        @NotNull(message = "count must be not null")
        @Min(value = 1, message = "count must be minimum 1")
        Integer count,

        @ElementCollection
        List<Long> requiredSkillIds
) {

}
