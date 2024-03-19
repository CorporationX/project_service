package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}