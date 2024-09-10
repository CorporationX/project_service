package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto) {
        validateProjectDto(projectDto);
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(),projectDto.getProjectName())){
            throw new IllegalArgumentException("Project with this name already exists for the owner");
        }
        projectDto.setProjectStatus(ProjectStatus.CREATED);
        Project project = projectMapper.toEntity(projectDto);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto updateProject(long id, ProjectDto projectDto) {
        validateProjectDto(projectDto);
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("This project doesn't exist");
        }
        Project project = projectMapper.toEntity(projectDto);
        project.setDescription(project.getDescription());
        project.setStatus(projectDto.getProjectStatus());
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getAllProjectsByFilter(String projectName, ProjectStatus projectStatus) {
        return projectRepository.findAll().stream()
                .filter(project -> {
                    boolean isVisible = project.getVisibility() == ProjectVisibility.PUBLIC;
                    if (!isVisible) {
                        return false;
                    }
                    return projectName == null || project.getName().contains(projectName) &&
                            projectStatus == null || project.getStatus().equals(projectStatus);
                })
                .map(projectMapper::toDto)
                .toList();
    }
    public List<ProjectDto> getAllProjects() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }

    public ProjectDto findProjectById(long projectId) {
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    private void validateProjectDto(ProjectDto projectDto) {
        if (projectDto.getProjectName() == null || projectDto.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Project name can't be empty");
        }
        if (projectDto.getProjectDescription() == null || projectDto.getProjectDescription().isEmpty()) {
            throw new IllegalArgumentException("Project description can't be empty");
        }
    }
}
