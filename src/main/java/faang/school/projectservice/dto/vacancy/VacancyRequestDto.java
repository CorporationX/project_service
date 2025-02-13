package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.List;

@Builder
public record VacancyRequestDto(
        @NotNull @NotBlank
        String name,

        @NotNull @NotBlank
        String description,

        @NotNull
        TeamRole position,

        @NotNull
        Long projectId,

        List<Long> candidatesIds,

        @NotNull
        Long createdBy,

        Long updatedBy,

        @NotNull
        VacancyStatus status,

        @NotNull
        Integer count) {}