package faang.school.projectservice.service.presentation.stats;

import faang.school.projectservice.dto.project.stats.ProjectStatsDto;
import faang.school.projectservice.model.Project;

public interface ProjectPresentationStatsService {
    ProjectStatsDto calculate(Project project);
}
