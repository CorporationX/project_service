package faang.school.projectservice.dto.moment;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateMomentDto(
        String name,
        String description,
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> userIds,
        LocalDateTime updatedAt,
        Long updatedBy
) {
}
