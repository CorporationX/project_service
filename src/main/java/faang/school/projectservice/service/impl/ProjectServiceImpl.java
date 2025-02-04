package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepositoryAdapter;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepositoryAdapter projectRepositoryAdapter;

    @Override
    public Project getProjectById(long projectId) {
        return projectRepositoryAdapter.getProjectById(projectId);
    }
}
