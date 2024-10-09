package faang.school.projectservice.model.dto.stage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record StageDto(
        Long stageId,

        @NotBlank(message = "Stage name can not be null or empty")
        String stageName,

        @Positive
        Long projectId,

        @NotNull
        List<@Valid StageRolesDto> stageRolesDto) {
}
