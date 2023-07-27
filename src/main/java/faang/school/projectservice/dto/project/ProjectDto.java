package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentProjectId;
    private List<Long> childrenIds;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private List<Long> stagesId;
}
