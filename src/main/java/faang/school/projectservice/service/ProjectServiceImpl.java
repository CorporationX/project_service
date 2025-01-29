package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.findByName(projectDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Project with this name already exists");
        }
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        project.setOwnerId(projectDto.getOwnerId());
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Optional<Project> optionalProject = projectRepository.findById(id);

        if (optionalProject.isEmpty()) {
            throw new IllegalArgumentException("Project not found");
        }

        Project project = optionalProject.get();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStatus(projectDto.getStatus());
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(project);

        return projectMapper.toDto(updatedProject);
    }

    public List<ProjectDto> getAllProjects(String name, ProjectStatus status, Boolean isPrivate, Long userId) {
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .filter(p -> (name == null || p.getName().equals(name)) &&
                        (status == null || p.getStatus() == status) &&
                        (isPrivate == null || p.getVisibility() == (isPrivate ? ProjectVisibility.PRIVATE : ProjectVisibility.PUBLIC)) &&
                        (userId == null || p.getOwnerId().equals(userId)))
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);

        if (optionalProject.isEmpty()) {
            throw new IllegalArgumentException("Project not found");
        }

        return projectMapper.toDto(optionalProject.get());
    }

}