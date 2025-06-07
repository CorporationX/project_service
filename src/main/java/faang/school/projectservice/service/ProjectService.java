package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto) {

        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project with this name already exists for the user.");
        }

        projectDto.setStatus("CREATED");

        Project project = projectMapper.toProject(projectDto);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectDto(savedProject);
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {

        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            throw new DataValidationException("Project not found.");
        }

        Project project = optionalProject.get();
        project.setDescription(projectDto.getDescription());
        project.setStatus(ProjectStatus.valueOf(projectDto.getStatus()));

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectDto(updatedProject);
    }

    public List<ProjectDto> getAllProjects(String name, String status) {

        List<Project> projects = projectRepository.findAll();
        return projectMapper.toProjectDtoList(projects);
    }

    public ProjectDto getProjectById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            throw new DataValidationException("Project not found.");
        }
        return projectMapper.toProjectDto(optionalProject.get());
    }
}