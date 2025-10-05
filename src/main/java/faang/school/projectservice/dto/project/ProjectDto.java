package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    private Long ownerId;
    private String name;
    private ProjectStatus status;
    private String description;
    private boolean isPrivate;
}
