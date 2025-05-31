package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class ProjectForCreationDto extends ProjectDto{

    @NotBlank(message = "Every project should have a name and a description")
    private String name;
    @NotBlank(message = "Every project should have a name and a description")
    private String description;
    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
