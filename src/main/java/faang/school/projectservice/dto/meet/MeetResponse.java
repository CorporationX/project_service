package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link faang.school.projectservice.model.Meet}
 */
@Builder
public record MeetResponse(long id, String title, String description, MeetStatus status, long creatorId, Long projectId,
                           List<Long> userIds, LocalDateTime startsAt) {
}