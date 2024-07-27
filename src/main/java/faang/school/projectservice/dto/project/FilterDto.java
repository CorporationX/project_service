package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilterDto {
    @Schema(description = "Filter pattern", example = "ABC", nullable = true)
    private String name;
    @Schema(description = "Filter by parent project", example = "1", nullable = true)
    private Long parentProjectId;
    @Schema(description = "Creation date filter", example = "2024-07-27 01:00:35.550300", nullable = true)
    private LocalDateTime createdAt;
    @Schema(description = "Updating date filter", example = "2024-07-27 01:00:35.550300", nullable = true)
    private LocalDateTime updatedAt;
    @Schema(description = "Status filter", example = "CREATED", nullable = true)
    private ProjectStatus status;
    @Schema(description = "Visibility filter", example = "PUBLIC", nullable = true)
    private ProjectVisibility visibility;
}
