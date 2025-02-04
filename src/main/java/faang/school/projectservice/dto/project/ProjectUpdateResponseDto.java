package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectUpdateResponseDto {
    private Long id;
    private ProjectStatus status;
    private String description;
    private LocalDateTime updatedAt;
}
