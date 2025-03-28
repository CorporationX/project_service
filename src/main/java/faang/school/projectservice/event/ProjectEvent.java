package faang.school.projectservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectEvent {
    private Long userId;
    private Long projectId;
}
