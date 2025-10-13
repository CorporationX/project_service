package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateStageDto(@NotNull Long stageId,
                             @NotBlank String stageName,
                             List<StageRoleDto> requiredRoles,
                             List<Long> executorIds) {
}
