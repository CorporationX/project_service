package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateStageRequest(
        @NotBlank String stageName,
        @NotNull Long projectId,
        List<StageRolesDto> stageRolesDto
) {
}
