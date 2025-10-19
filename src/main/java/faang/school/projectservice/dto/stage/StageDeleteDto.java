package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.NotNull;

public record StageDeleteDto(
        @NotNull Long projectId,
        @NotNull Long stageId
){
}