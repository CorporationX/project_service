package faang.school.projectservice.model.dto.meet;

import faang.school.projectservice.model.entity.meet.MeetStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MeetResponseDto(
        long id,
        String title,
        String description,
        MeetStatus status,
        long creatorId,
        long projectId,
        List<Long> userIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}