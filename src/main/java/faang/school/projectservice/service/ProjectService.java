package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        checkProjectNotExist(subProjectDto);
        checkSubProjectNotPublicOnPrivateProject(subProjectDto.getParentProjectId(), subProjectDto.getVisibility(), subProjectDto.getName());
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        projectRepository.save(subProject);
        return subProjectMapper.toDto(subProject);
    }

    public LocalDateTime updateSubProject(UpdateSubProjectDto updateSubProjectDto) {
        Project projectToUpdate = projectRepository.getProjectById(updateSubProjectDto.getId());

        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            return closeSubProject(updateSubProjectDto, projectToUpdate);
        }
        checkUpdatedVisibility(updateSubProjectDto, projectToUpdate);
        setAllNeededFields(updateSubProjectDto, projectToUpdate);
        projectRepository.save(projectToUpdate);
        return projectToUpdate.getUpdatedAt();
    }

    public List<SubProjectDto> getProjectChildrenWithFilter(ProjectFilterDto projectFilterDto, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Project> subProjectsStream = project.getChildren().stream();
        List<ProjectFilter> applicableFilters = projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .toList();
        for (ProjectFilter filter : applicableFilters) {
            subProjectsStream = filter.apply(subProjectsStream, projectFilterDto);
        }
        return subProjectsStream.map(subProjectMapper::toDto)
                .toList();
    }

    private LocalDateTime closeSubProject(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        projectToUpdate.getChildren().forEach(this::checkProjectStatusCompleteOrCancelled);
        setAllNeededFields(updateSubProjectDto, projectToUpdate);
        projectRepository.save(projectToUpdate);
        return projectToUpdate.getUpdatedAt();
    }

    private void checkUpdatedVisibility(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        ProjectVisibility newVisibility = updateSubProjectDto.getVisibility();
        String projectName = projectToUpdate.getName();

        if (newVisibility == ProjectVisibility.PUBLIC) {
            if (updateSubProjectDto.getParentProjectId() != null) {
                checkSubProjectNotPublicOnPrivateProject(updateSubProjectDto.getParentProjectId(), newVisibility, projectName);
            } else {
                checkSubProjectNotPublicOnPrivateProject(projectToUpdate.getParentProject().getId(), newVisibility, projectName);
            }
        }
        if (newVisibility == ProjectVisibility.PRIVATE) {
            makeAllSubprojectPrivate(projectToUpdate);
        }
    }

    private void makeAllSubprojectPrivate(Project project) {
        List<Project> children = project.getChildren();

        if (children != null && !children.isEmpty()) {
            children.forEach(this::makeAllSubprojectPrivate);
        }
        project.setVisibility(ProjectVisibility.PRIVATE);
        projectRepository.save(project);
    }

    private void setAllNeededFields(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        if (updateSubProjectDto.getParentProjectId() != null) {
            Project newParentProject = projectRepository.getProjectById(updateSubProjectDto.getParentProjectId());
            projectToUpdate.setParentProject(newParentProject);
        }
        if (updateSubProjectDto.getName() != null) {
            projectToUpdate.setName(updateSubProjectDto.getName());
        }
        if (updateSubProjectDto.getDescription() != null) {
            projectToUpdate.setDescription(updateSubProjectDto.getDescription());
        }
        if (updateSubProjectDto.getStatus() != null) {
            projectToUpdate.setStatus(updateSubProjectDto.getStatus());
        }
        if (updateSubProjectDto.getVisibility() != null) {
            projectToUpdate.setVisibility(updateSubProjectDto.getVisibility());
        }
    }

    private void checkSubProjectNotPublicOnPrivateProject(long parentProjectId, ProjectVisibility visibility, String projectName) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);
        boolean isParentProjectPrivate = parentProject.getVisibility().equals(ProjectVisibility.PRIVATE);
        boolean isSubProjectPublic = visibility.equals(ProjectVisibility.PUBLIC);
        if (isParentProjectPrivate && isSubProjectPublic) {
            throw new DataValidationException(String.format("Public SubProject: %s, cant be with a private parent Project with id: %d", projectName, parentProject.getId()));
        }
    }

    private void checkProjectNotExist(SubProjectDto SubProjectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(SubProjectDto.getOwnerId(), SubProjectDto.getName())) {
            throw new DataAlreadyExistingException(String.format("Project %s is already exist", SubProjectDto.getName()));
        }
    }

    private void checkProjectStatusCompleteOrCancelled(Project project) {
        if (project.getStatus() != ProjectStatus.COMPLETED && project.getStatus() != ProjectStatus.CANCELLED) {
            throw new DataValidationException("Can't close project if subProject status are not complete or cancelled");
        }
    }
}