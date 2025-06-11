package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Projects filters")
public class ProjectFilterDto {
    @Size(max = 255, message = "Name must be at most 255 characters long")
    @Schema(description = "Project name filter")
    private String name;
    @Size(max = 255, message = "Description must be at most 255 characters long")
    @Schema(description = "Project description pattern filter")
    private String descriptionPattern;
    @Schema(description = "Project owner filter by user id")
    private Long ownerId;
    @Schema(description = "Project status filter", allowableValues = {"CREATED", "IN_PROGRESS", "COMPLETED", "ON_HOLD", "CANCELLED"})
    private ProjectStatus status;
    @Schema(description = "Project visibility filter", allowableValues = {"PUBLIC", "PRIVATE"})
    private ProjectVisibility visibility;
}
