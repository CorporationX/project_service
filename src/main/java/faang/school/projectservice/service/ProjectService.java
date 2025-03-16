package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;

    public ProjectDto created(ProjectDto projectDto, long creatorId) {
        projectRepository.existsByOwnerIdAndName(creatorId, projectDto.getName());

        projectDto.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));
        return projectMapper.toDto(savedProject);
    }

    public ProjectDto update(long projectId, ProjectDto projectDto) {

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new DataValidationException("Проекта не существует!"));

        projectMapper.updateProjectFromDto(projectDto, project);
        Project result = projectRepository.save(project);

        return projectMapper.toDto(result);
    }

    public List<ProjectDto> findProjectsByFiltersAndAccess (){

    }
}