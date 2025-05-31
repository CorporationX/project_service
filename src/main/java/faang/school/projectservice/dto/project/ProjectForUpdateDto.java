package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class ProjectForUpdateDto extends ProjectDto {
    @NotNull(message = "Please indicate which project should be changed")
    private Long id;
    @NotBlank(message = "Every project should have a name and a description")
    private String name;
    @NotBlank(message = "Every project should have a name and a description")
    private String description;
    @NotBlank(message = "Please choose previous status or indicate new one")
    private ProjectStatus status;
    @NotBlank(message = "Please choose previous visibility type or indicate new one")
    private ProjectVisibility visibility;
}
