package faang.school.projectservice.dto.moment;

import java.time.LocalDateTime;
import java.util.List;

public record SearchMomentDto(
        Long id,
        String name,
        String description,
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> userIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy
) {
}
