package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;

public interface ProjectService {
    Project getProjectById(Long projectId);
    Project save(Project project);
}
