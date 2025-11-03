package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateMomentDto(
        @NotBlank String name,
        @NotBlank String description,
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> userIds,
        LocalDateTime updatedAt,
        Long updatedBy
) {
}
