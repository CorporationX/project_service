package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record StageDto(
        @NotNull Long stageId,
        @NotBlank String stageName,
        @NotNull Long projectId,
        @NotNull @Size(min = 1) List<StageRoleDto> requiredRoles,
        List<TeamMemberDto> executors,
        int tasksCount
) {
}
