package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.NotNull;

public record StageRequestDeleteDto(
        @NotNull Long projectId,
        @NotNull Stage stage
){
}