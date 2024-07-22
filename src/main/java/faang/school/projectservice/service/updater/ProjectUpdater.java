package faang.school.projectservice.service.updater;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;

public interface ProjectUpdater {
    boolean isApplicable(ProjectDto updates);

    void apply(Project project, ProjectDto updates);
}
