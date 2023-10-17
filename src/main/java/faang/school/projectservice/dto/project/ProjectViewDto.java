package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ProjectViewDto {
    private Long projectId;
    private Long userId;
    private LocalDateTime dateTime;
}
