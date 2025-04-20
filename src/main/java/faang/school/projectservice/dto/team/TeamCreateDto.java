package faang.school.projectservice.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TeamCreateDto(

        @NotNull
        Long projectId
) {
}
