package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StageRequestCreateDto(
        @NotNull
        Long projectId,
        @NotNull @NotBlank
        String stageName,
        @NotNull @NotEmpty
        List<@NotNull TeamRole> teamRoles,
        @NotNull @NotEmpty
        List<@NotNull Long> executorsId
) {
}