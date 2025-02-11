package faang.school.projectservice.dto.moment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreateMomentResponse(Long id,
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
