package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentCreateRequestDto(
        @NotBlank
        String name,
        String description,
        LocalDateTime date,
        @NotNull
        List<Long> projectIds,
        List<Long> teamMemberIds
) {
}