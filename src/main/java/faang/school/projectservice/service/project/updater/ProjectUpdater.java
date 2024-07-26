package faang.school.projectservice.service.project.updater;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

public interface ProjectUpdater {
    boolean isApplicable(ProjectDto updates);

    void apply(Project project, ProjectDto updates);
}
