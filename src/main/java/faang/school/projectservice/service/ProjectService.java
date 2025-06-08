package faang.school.projectservice.service;


import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Project createProject(Project project) {
        if (projectRepository.existsByOwnerIdAndName(project.getOwnerId(), project.getName())) {
            throw new DataValidationException("Project with this name already exists for the user.");
        }

        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long id, Project projectUpdateData) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Project not found."));

        project.setDescription(projectUpdateData.getDescription());
        project.setStatus(projectUpdateData.getStatus());
        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Project not found."));
    }
}