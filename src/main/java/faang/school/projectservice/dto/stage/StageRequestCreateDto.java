package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StageRequestCreateDto(
        @NotNull Project project,
        @NotNull @NotBlank String stageName,
        @NotNull List<TeamRole> teamRoles,
        @NotNull List<TeamMember> executors
        ) {
}