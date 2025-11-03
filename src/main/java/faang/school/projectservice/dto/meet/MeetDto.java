package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@FieldNameConstants
@Builder
public record MeetDto(
        Long id,
        String title,
        String description,
        MeetStatus status,
        long creatorId,
        long projectId,
        List<Long> userIds,
        LocalDateTime startsAt
) {
}