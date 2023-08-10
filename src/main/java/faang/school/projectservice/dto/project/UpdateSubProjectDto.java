package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateSubProjectDto {
    private Long id;
    private String name;
    private String description;
    private long ownerId;
    private Long parentProjectId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}