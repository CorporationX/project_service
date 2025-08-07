package faang.school.projectservice.dto.presentation;

import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.stats.ProjectStatsDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.team.TeamDto;

import java.util.List;

public record ProjectPresentationDto(
        ProjectInfoDto project,
        List<TeamDto> teams,
        List<TaskDto> tasks,
        ProjectStatsDto stats
) {
}