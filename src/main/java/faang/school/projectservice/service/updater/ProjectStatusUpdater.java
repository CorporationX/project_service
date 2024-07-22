package faang.school.projectservice.service.updater;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;

public class ProjectStatusUpdater implements ProjectUpdater {
    @Override
    public boolean isApplicable(ProjectDto updates) {
        return updates.getStatus() != null;
    }

    @Override
    public void apply(Project project, ProjectDto updates) {
        project.setStatus(updates.getStatus());
    }
}
