package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class SubProjectDto {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private Project parentProject;
}
