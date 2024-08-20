package faang.school.projectservice.dto.project;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class ProjectViewEvent {
    private long userId;
    private long projectId;
    private LocalDateTime eventTime;
}
