package faang.school.projectservice.dto.project.stage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record StageUpdateDto(
        String stageName,

        @NotNull(message = "Executor ids is required")
        List<Long> executorIds) {
}
