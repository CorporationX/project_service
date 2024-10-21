package faang.school.projectservice.dto.project.stage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record StageUpdateDto(
        @Size(max = 255, message = "Stage name cannot be longer than 128 characters") String stageName,

        @NotNull(message = "Executor ids is required") List<@NotNull(message = "Executor ID must not be null") @Positive(message = "Executor ID must be positive") Long> executorIds) {
}
