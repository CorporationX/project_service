package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record MomentDto(
        @NotNull Long id,
        @NotBlank String name,
        @NotBlank String description,
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> userIds,
        String imageId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @NotBlank Long createdBy,
        Long updatedBy
) {
}
