package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Optional<Project> getProjectByIdOrEmpty(long projectId) {
        return projectRepository.findById(projectId);
    }
}
