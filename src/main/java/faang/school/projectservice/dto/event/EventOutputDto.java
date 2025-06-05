package faang.school.projectservice.dto.event;

import faang.school.projectservice.model.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventOutputDto {
    private long id;
    private long projectId;
    private String title;
    private String description;
    private long creatorId;
    private EventStatus status;
    private List<Long> userIds;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}