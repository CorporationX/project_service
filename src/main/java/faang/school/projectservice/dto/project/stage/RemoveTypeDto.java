package faang.school.projectservice.dto.project.stage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemoveTypeDto(

        @NotNull(message = "Remove strategy is required")
        RemoveStrategy removeStrategy,

        Long stageForMigrateId) {
}
