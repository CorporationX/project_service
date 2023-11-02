package faang.school.projectservice.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectViewEvent {
    private Long projectId;
    private Long userId;
    private LocalDateTime timestamp;
}
