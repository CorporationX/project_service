package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectDto {
    private Long id;
    private String description;
    private String name;
    private long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}
