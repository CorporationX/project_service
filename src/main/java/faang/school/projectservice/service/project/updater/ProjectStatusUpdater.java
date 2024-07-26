package faang.school.projectservice.service.project.updater;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

@Component
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
