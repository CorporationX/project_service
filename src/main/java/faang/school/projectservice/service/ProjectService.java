package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;

    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format("Project %s already exist", projectDto.getName()));
        }
        Project project = projectMapper.toModel(projectDto);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto update(ProjectDto projectDto, long projectId) {
        Project projectToUpdate = projectRepository.getProjectById(projectId);
        if (projectDto.getDescription() != null) {
            projectToUpdate.setDescription(projectDto.getDescription());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        if (projectDto.getStatus() != null) {
            projectToUpdate.setStatus(projectDto.getStatus());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }
}
