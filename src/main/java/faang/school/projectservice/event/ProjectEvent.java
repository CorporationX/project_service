package faang.school.projectservice.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectEvent {
    private long projectId;
    private long userId;
    private LocalDateTime createAt;
}
