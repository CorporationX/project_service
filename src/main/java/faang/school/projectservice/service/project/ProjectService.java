package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto getProject(long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);

        return project.map(projectMapper::toDto).orElse(null);
    }

    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        List<Project> projects = projectRepository.findAllByIds(ids);

        return projects.stream().map(projectMapper::toDto).toList();
    }
}
