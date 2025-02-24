package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record MomentUpdateRequestDto(
        @NotBlank
        String name,
        String description,
        List<Long> projectToAddIds,
        List<Long> teamMemberToAddIds
) {
}