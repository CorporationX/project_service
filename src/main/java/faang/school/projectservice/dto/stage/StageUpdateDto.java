package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record StageUpdateDto(
        @NotNull @Min(1) Long countParticipant,
        @NotNull TeamRole teamRole
) {
}