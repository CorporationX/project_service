package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@Schema(description = "Project for update")
public class ProjectForUpdateDto extends ProjectDto {
    @NotNull(message = "Please indicate which project should be changed")
    @Schema(description = "Existing project id")
    private Long id;
    @NotBlank(message = "Every project should have a name and a description")
    @Schema(description = "New project name")
    private String name;
    @NotBlank(message = "Every project should have a name and a description")
    @Schema(description = "New project description")
    private String description;
    @NotBlank(message = "Please choose previous status or indicate new one")
    @Schema(description = "New project status", allowableValues = {"CREATED", "IN_PROGRESS", "COMPLETED", "ON_HOLD", "CANCELLED"}, accessMode = Schema.AccessMode.READ_ONLY)
    private ProjectStatus status;
    @NotBlank(message = "Please choose previous visibility type or indicate new one")
    @Schema(description = "New project visibility", allowableValues = {"PUBLIC", "PRIVATE"})
    private ProjectVisibility visibility;
}
