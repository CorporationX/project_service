package faang.school.projectservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectViewEvent {
    private long projectId;
    private long userId;
    private LocalDateTime timestamp;
}
