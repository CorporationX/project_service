package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mappers.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.findAll().stream().anyMatch(x ->
                x.getOwnerId().equals(projectDto.getOwnerId())
                        && x.getName().equals(projectDto.getName()))) {
            throw new DataValidationException("The project with " + projectDto.getName() + " name already exists");
        }
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        if (project == null) {
            throw new EntityNotFoundException("The project with that id does not exist");
        }
        projectMapper.updateProjectFromDto(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.getProjectById(id);
        if (project == null) {
            throw new EntityNotFoundException("The project with that id does not exist");
        }
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getAllProject() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }
}
