package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record DeleteStageRequest(
        Long stageId,
        @NotBlank String stageName,
        @NotNull Long projectId,
        List<StageRolesDto> stageRolesDto,
        String deletionStrategy,
        Long targetStageId
) {
}