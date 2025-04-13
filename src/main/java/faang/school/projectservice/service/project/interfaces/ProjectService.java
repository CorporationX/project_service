package faang.school.projectservice.service.project.interfaces;

import faang.school.projectservice.model.Project;

public interface ProjectService {

    public Project getProjectById(Long projectId);

    public void save(Project project);
}
