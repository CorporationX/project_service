package faang.school.projectservice.service;

import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;


    public ProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto createSubProjectDto) {
        try {
            Project parentProject = projectRepository.getProjectById(parentProjectId);

            Project project = projectMapper.toEntity(createSubProjectDto);

            if (parentProject.getVisibility() == ProjectVisibility.PUBLIC && createSubProjectDto.getIsPrivate()) {
                throw new IllegalArgumentException("Private projects are not allowed");
            }

            project.setParentProject(parentProject);
            Project savedProject = projectRepository.save(project);
            return projectMapper.toDto(savedProject);

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Parent project not found with ID: " + parentProjectId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid project data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating subproject", e);
            throw new RuntimeException("An unexpected error occurred while creating the subproject.");
        }
    }

    public ProjectDto updateSubProject(Long subProjectId, CreateSubProjectDto updateSubProjectDto) {
        Project subProject = projectRepository.getProjectById(subProjectId);
        if (subProject == null) {
            throw new EntityNotFoundException(String.format("Subproject with ID %d not found", subProjectId));
        }
        Project parentProject = subProject.getParentProject();
        if (parentProject != null && parentProject.getStatus() == ProjectStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update subproject because the parent project is already closed.");
        }

        subProject.setName(updateSubProjectDto.getName());
        subProject.setDescription(updateSubProjectDto.getDescription());

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE){
            subProject.setVisibility(ProjectVisibility.PRIVATE);
        }

        if (parentProject.getStatus() == ProjectStatus.COMPLETED){
            List<Project> children = parentProject.getChildren();
            boolean hasOpenSubProjects = children.stream().anyMatch(sub ->
                    sub.getStatus() == ProjectStatus.CREATED ||
                            sub.getStatus() == ProjectStatus.IN_PROGRESS ||
                            sub.getStatus() == ProjectStatus.ON_HOLD
            );

            if (hasOpenSubProjects) {
                throw new IllegalStateException("Cannot close parent project because there are open subprojects.");
            }
        }

        try {
            Project updatedSubProject = projectRepository.save(subProject);

            return projectMapper.toDto(updatedSubProject);
        } catch (Exception e) {
            log.error("Error updating subproject with ID {}: {}", subProjectId, e.getMessage());
            throw new RuntimeException("Failed to update subproject", e);
        }
    }


    public ProjectDto getSubProject(Long parentProjectId, Long subProjectId) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);

        if (parentProject == null) {
            throw new EntityNotFoundException("Parent project with ID " + parentProjectId + " not found.");
        }

        boolean isSubProjectVisible = parentProject.getChildren().stream()
                .anyMatch(subProject -> subProject.getVisibility() == ProjectVisibility.PRIVATE);

        if (isSubProjectVisible) {
            throw new EntityNotFoundException("Cannot find subproject because it is private.");
        }

        if (parentProject.getChildren().isEmpty()) {
            throw new EntityNotFoundException("Parent project with ID " + parentProjectId + " has no subprojects.");
        }

        return parentProject.getChildren().stream()
                .filter(project -> project.getId().equals(subProjectId))
                .findFirst()
                .map(projectMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Subproject with ID " + subProjectId + " not found"));
    }

}
