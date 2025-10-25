package faang.school.projectservice.dto.moment;

import java.time.LocalDateTime;
import java.util.List;

public record MomentDto(
    Long id,
    String name,
    String description,
    LocalDateTime date,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<Long> projectIds,
    List<Long> memberIds
) {
    public MomentDto {
        projectIds = projectIds == null ? List.of() : projectIds;
        memberIds = memberIds == null ? List.of() : memberIds;
    }
}
