package faang.school.projectservice.dto.client.subproject;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

@Data
public class CreateSubProjectDto {
    private Long id;
    private String name;
    private ProjectStatus status = ProjectStatus.CREATED;
    private ProjectVisibility visibility;
    private Project parentProject;
    private Long ownerId;
}
