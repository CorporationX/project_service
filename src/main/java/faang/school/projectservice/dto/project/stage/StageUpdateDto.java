package faang.school.projectservice.dto.project.stage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record StageUpdateDto(

        @NotNull(message = "Stage id is required")
        @Positive(message = "Stage id must be positive")
        Long stageId,

        String stageName,

        @NotNull(message = "Executor ids is required")
        List<Long> executorIds) {}
