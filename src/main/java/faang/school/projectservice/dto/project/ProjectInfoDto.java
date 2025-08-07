package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed information about the project")
public record ProjectInfoDto(

        @Schema(description = "Project name")
        String name,

        @Schema(description = "Project description")
        String description,

        @Schema(description = "Project creation date")
        String createdAt,

        @Schema(description = "Current status of the project")
        ProjectStatus status,

        @Schema(description = "Username of the project owner")
        String ownerUsername,

        @Schema(description = "Name of the parent project, if exists")
        String parentProjectName,

        @Schema(description = "Names of child projects")
        List<String> childrenProjectNames
) {
}