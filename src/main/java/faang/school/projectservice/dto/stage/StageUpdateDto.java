package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record StageUpdateDto(
        @Positive(message = "The number of required participants must not be less than zero!")
        @NotNull @Min(1) Long countParticipant,
        @NotNull TeamRole teamRole
) {
}