package faang.school.projectservice.dto.moment;

import java.time.LocalDateTime;
import java.util.List;

public record GetMomentResponse(Long id,
                                String name,
                                String description,
                                LocalDateTime date,
                                List<Long> projectIds,
                                List<Long> resourceIds,
                                List<Long> userIds,
                                String imageId,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt,
                                Long createdBy,
                                Long updatedBy) {
}
