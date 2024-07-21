package faang.school.projectservice.service.updater;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.updater.ProjectUpdaterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.updater.ProjectUpdater;

public class ProjectStatusUpdater implements ProjectUpdater {
    @Override
    public boolean isApplicable(ProjectUpdaterDto updates) {
        return updates.getStatus() != null;
    }

    @Override
    public void apply(Project project, ProjectUpdaterDto updates) {
        project.setStatus(updates.getStatus());
    }
}
