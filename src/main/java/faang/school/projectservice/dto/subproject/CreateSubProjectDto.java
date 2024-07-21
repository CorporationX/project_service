package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
