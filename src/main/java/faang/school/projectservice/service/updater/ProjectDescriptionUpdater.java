package faang.school.projectservice.service.updater;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.updater.ProjectUpdaterDto;
import faang.school.projectservice.model.Project;

public class ProjectDescriptionUpdater implements ProjectUpdater{
    @Override
    public boolean isApplicable(ProjectUpdaterDto updates) {
        return updates.getDescription() != null;
    }

    @Override
    public void apply(Project project, ProjectUpdaterDto updates) {
        project.setDescription(updates.getDescription());
    }
}
