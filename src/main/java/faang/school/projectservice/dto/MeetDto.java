package faang.school.projectservice.dto;

import faang.school.projectservice.model.MeetStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MeetDto(
        Long id,
        String title,
        String description,
        MeetStatus status,
        Long creatorId,
        Long projectId,
        LocalDateTime startsAt
) {
}
