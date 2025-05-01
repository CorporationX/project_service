package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectViewEvent {
    private long projectId;
    private long userId;
    private LocalDateTime timestamp;
}
