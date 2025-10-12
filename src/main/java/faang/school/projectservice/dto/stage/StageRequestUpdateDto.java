package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StageRequestUpdateDto(
        @NotNull Stage stage,
        @NotNull @Min(1)  Long countParticipant,
        @NotNull TeamRole teamRole
){
}