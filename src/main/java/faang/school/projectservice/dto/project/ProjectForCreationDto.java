package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@Schema(description = "Project for create")
public class ProjectForCreationDto extends ProjectDto{
    @NotBlank(message = "Every project should have a name and a description")
    @Schema(description = "New project name")
    private String name;
    @NotBlank(message = "Every project should have a name and a description")
    @Schema(description = "New project description")
    private String description;
    @Schema(description = "New project owner user id")
    private Long ownerId;
    @Schema(description = "New project status", allowableValues = {"CREATED", "IN_PROGRESS", "COMPLETED", "ON_HOLD", "CANCELLED"}, accessMode = Schema.AccessMode.READ_ONLY)
    private ProjectStatus status;
    @Schema(description = "New project visibility", allowableValues = {"PUBLIC", "PRIVATE"})
    private ProjectVisibility visibility;
}
