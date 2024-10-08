package faang.school.projectservice.dto.project.stage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record RemoveTypeDto(

        @NotNull(message = "Remove strategy is required")
        RemoveStrategy removeStrategy,

        @Positive(message = "Stage for migrate ID must be positive")
        Long stageForMigrateId) {
}
