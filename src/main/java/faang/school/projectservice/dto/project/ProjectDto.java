package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private ProjectStatus status;
}
