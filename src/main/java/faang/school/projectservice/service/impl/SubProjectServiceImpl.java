package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.dto.CreateSubProjectDto;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectStatus;
import faang.school.projectservice.model.enums.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.service.SubProjectService;
import faang.school.projectservice.validator.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class SubProjectServiceImpl implements SubProjectService {
    private final SubProjectMapper subProjectMapper;
    private final ValidatorService validatorService;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> filters;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        CreateSubProjectDto subProjectDto = subProjectMapper.mapToSubDto(projectDto);
        subProjectDto.setStatus(ProjectStatus.CREATED);
        validatorService.isProjectExists(subProjectDto.getParentProjectId());
        Project parentProject = projectRepository.findById(subProjectDto.getParentProjectId());
        validatorService.isProjectExistsByName(subProjectDto.getName());
        validatorService.isVisibilityRight(parentProject.getVisibility(), subProjectDto.getVisibility());
        Project projectToSaveDb = subProjectMapper.mapToEntity(subProjectDto);
        projectToSaveDb.setParentProject(parentProject);
        Project result = projectRepository.save(projectToSaveDb);

        return subProjectMapper.mapToProjectDto(result);
    }

    @Override
    public List<ProjectDto> getFilteredSubProjects(ProjectDto projectDto) {
        validatorService.isProjectExists(projectDto.getId());
        List<Project> allChildren = projectRepository.findAll();
        List<Project> allSubProjects = allChildren.stream()
                .filter(project -> project.getParentProject() != null)
                .filter(project -> Objects.equals(project.getParentProject().getId(), projectDto.getId()))
                .toList();
        if (allSubProjects.isEmpty()) {
            return new ArrayList<>();
        }
        List<Project> allFilteredProjects = filters.stream()
                .filter(filter -> filter.isApplicable(projectDto))
                .reduce(allSubProjects.stream(), (stream, filter) -> filter.apply(stream, projectDto),
                        (s1, s2) -> s1)
                .filter(project -> project.getVisibility() != ProjectVisibility.PRIVATE)
                .toList();
        return allFilteredProjects.stream()
                .map(subProjectMapper::mapToProjectDto)
                .toList();
    }

    @Override
    public ProjectDto updatingSubProject(ProjectDto projectDto) {
        validatorService.isProjectExists(projectDto.getId());
        Project updateProject = projectRepository.findById(projectDto.getId());
        if (projectDto.getVisibility() != null) {
            updateProject = updateProjectVisibility(updateProject, projectDto.getVisibility());
        } else if (projectDto.getStatus() != null) {
            updateProject = updateProjectStatus(updateProject, projectDto.getStatus());
        }
        return subProjectMapper.mapToProjectDto(projectRepository.save(updateProject));
    }

    private Project updateProjectVisibility(Project project, ProjectVisibility projectVisibility) {
        if (projectVisibility == ProjectVisibility.PUBLIC) {
            project.setVisibility(projectVisibility);
            project.setUpdatedAt(LocalDateTime.now());
            return project;
        }
        List<Project> children = project.getChildren();
        children.forEach(child -> child.setVisibility(projectVisibility));
        project.setVisibility(projectVisibility);
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }

    private Project updateProjectStatus(Project project, ProjectStatus projectStatus) {
        if (projectStatus != ProjectStatus.COMPLETED) {
            project.setStatus(projectStatus);
            project.setUpdatedAt(LocalDateTime.now());
            return project;
        }
        for (var childProject : project.getChildren()) {
            if (!childProject.getStatus().equals(projectStatus)) {
                throw new IllegalStateException("Can't close project with id " + project.getId() + ", because children project still open");
            }
        }

        //TODO получаем Moment

        project.setStatus(projectStatus);
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
}
