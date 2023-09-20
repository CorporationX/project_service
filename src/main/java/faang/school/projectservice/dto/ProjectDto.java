package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Project")
public class ProjectDto {
    @Schema(name = "Id")
    private Long id;
    @Schema(name = "Name")
    @NotBlank
    private String name;
    @Schema(name = "Description")
    @NotBlank
    private String description;
    @Schema(name = "Owner Id")
    private Long ownerId;
    @Schema(name = "Status")
    private ProjectStatus status;
    @Schema(name = "Visibility")
    private ProjectVisibility visibility;
}
