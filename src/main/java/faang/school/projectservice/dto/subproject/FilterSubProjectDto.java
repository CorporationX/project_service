package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.dto.FilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "Filter for projects")
@Data
@Builder
public class FilterSubProjectDto extends FilterDto {
    @Schema(description = "Filter for project name", example = "Project name", nullable = true)
    private String name;

    @Schema(description = "Filter for project status", example = "CREATED", nullable = true)
    private ProjectStatus status;

    @Schema(description = "Filter for project visibility", example = "PUBLIC", nullable = true)
    private ProjectVisibility visibility;
}
