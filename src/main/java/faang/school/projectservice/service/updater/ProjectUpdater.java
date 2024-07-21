package faang.school.projectservice.service.updater;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.updater.ProjectUpdaterDto;
import faang.school.projectservice.model.Project;

public interface ProjectUpdater {
    boolean isApplicable(ProjectUpdaterDto updates);
    void apply(Project project, ProjectUpdaterDto updates);
}
