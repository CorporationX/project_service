package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

@Data
public class ProjectResponseDto {
    private Long id;
    private ProjectStatus status;
    private String name;
    private String description;
    private Long ownerId;
    private ProjectVisibility visibility;
}
