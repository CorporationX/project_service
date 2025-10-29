package faang.school.projectservice.dto.moment;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMomentDto(
        String name,
        String description,
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> userIds,
        String imageId,
        LocalDateTime createdAt,
        Long createdBy
) {
}
