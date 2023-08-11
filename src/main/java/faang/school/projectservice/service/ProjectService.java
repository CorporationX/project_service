package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private ProjectRepository projectRepository;

    public List<Project> getProjectsByIds(List<Long> projectIds) {
        return projectIds.stream()
                .map(id -> projectRepository.getProjectById(id))
                .toList();
    }
}
