package faang.school.projectservice.dto.presentation;

import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.stats.ProjectStatsDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.team.TeamDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO containing all data required for generating a project presentation")
public record ProjectPresentationDto(

        @Schema(description = "General information about the project")
        ProjectInfoDto project,

        @Schema(description = "List of teams participating in the project")
        List<TeamDto> teams,

        @Schema(description = "List of tasks related to the project")
        List<TaskDto> tasks,

        @Schema(description = "Statistical data about the project")
        ProjectStatsDto stats
) {
}