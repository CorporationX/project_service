package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto implements ProjectValidator {
    @Schema(description = "Unique identifier of the project", example = "1", nullable = true)
    private Long id;
    @Schema(description = "name of the project", example = "Project Name")
    private String name;
    @Schema(description = "IDs of the children projects", example = "[1,2,3,5]", nullable = true)
    private List<Long> childrenIds;
    @Schema(description = "Date of creation", example = "2024-07-27 01:00:35.550300", nullable = true)
    private LocalDateTime createdAt;
    @Schema(description = "Updating date", example = "2024-07-27 01:00:35.550300", nullable = true)
    private LocalDateTime updatedAt;
    @Builder.Default
    @Schema(description = "Status of the project", example = "CREATED", defaultValue = "CREATED")
    private ProjectStatus status = ProjectStatus.CREATED;
    @Builder.Default
    @Schema(description = "Status of the project", example = "PUBLIC", defaultValue = "PUBLIC")
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    @Schema(description = "Stages ids", example = "[1,2,3,5]", nullable = true)
    private List<Long> stagesIds;
    @Schema(description = "Teams Ids", example = "[1,2,3,5]", nullable = true)
    private List<Long> teamsIds;
}
