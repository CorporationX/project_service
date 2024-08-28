package faang.school.projectservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectViewEvent {
    private long projectId;
    private long userId;
    private LocalDateTime receivedAt;
}
