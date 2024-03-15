package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;


@Data
public class CreateSubProjectDto {
    private String name;
    private String description;
    private Long parentProjectId;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}
