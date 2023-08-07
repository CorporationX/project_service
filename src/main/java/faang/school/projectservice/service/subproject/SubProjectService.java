package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectCreateDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;

    public SubProjectCreateDto createProject(SubProjectCreateDto subProjectCreateDto) {
        Project project = subProjectMapper.toEntity(subProjectCreateDto);
        prepareProjectForCreate(project);

        Project newProject = projectRepository.save(project);
        return subProjectMapper.toDto(newProject);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isExistProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    private void prepareProjectForCreate(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);

        ProjectVisibility parentVisibility = project.getParentProject().getVisibility();
        project.setVisibility(parentVisibility);
    }
}
