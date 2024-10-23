package faang.school.projectservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectViewEvent {
    private Long projectId;
    private Long userId;
    private LocalDateTime viewTime;
}
