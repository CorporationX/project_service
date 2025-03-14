package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.CreateSubProjectMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.MomentType;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MomentRepository momentRepository;
    private final StageRepository stageRepository;
    private final CreateSubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;
    private final List<SubProjectFilter> subProjectFilters;

    public ProjectDto create(CreateSubProjectDto subProjectDto) {
        Project parentProject = validateAndRetrieveParentProject(subProjectDto);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        Project savedSubProject = projectRepository.save(subProject);

        addStages(savedSubProject, subProjectDto.getStages());
        addChildren(savedSubProject, subProjectDto.getChildren());

        return projectMapper.toDto(projectRepository.save(savedSubProject));
    }

    public ProjectDto update(ProjectDto projectDto) {
        projectDto.validateCommonFields();

        Project project = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Parent project with id = " + projectDto.getId() + " doesn't exist"));
        ProjectStatus status = projectDto.getStatus();
        ProjectVisibility visibility = projectDto.getVisibility();

        if (project.getChildren() != null && !project.getChildren().isEmpty()) {
            List<Project> subProjects = new ArrayList<>(project.getChildren());
            boolean areAllCompleted = subProjects.stream()
                    .allMatch(subProject -> subProject.getStatus() == ProjectStatus.COMPLETED);

            boolean isAnyPublic = subProjects.stream()
                    .anyMatch(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC);

            if (status == ProjectStatus.COMPLETED) {
                if (areAllCompleted) {
                    createMoment(project);
                } else {
                    throw new IllegalArgumentException("Not all subprojects are completed");
                }
            }
            if (visibility == ProjectVisibility.PRIVATE && isAnyPublic) {
                subProjects.stream()
                        .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC)
                        .forEach(subProject -> {
                            subProject.setVisibility(ProjectVisibility.PRIVATE);
                            projectRepository.save(subProject);
                        });
            }
        }

        project.setVisibility(visibility);
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());

        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getSubProjects(SubProjectsFilterDto filter) {
        var projectId = filter.projectId();
        if (projectId == null) {
            throw new IllegalArgumentException("Project id is required");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id = " + projectId + " doesn't exist"));

        if (project.getChildren() == null || project.getChildren().isEmpty()) {
            return Collections.emptyList();
        }

        Stream<Project> projects = project.getChildren().stream();

        for (SubProjectFilter subProjectFilter : subProjectFilters) {
            if (subProjectFilter.isApplicable(filter)) {
                projects = subProjectFilter.apply(projects, filter);
            }
        }

        return projects
                .map(projectMapper::toDto)
                .toList();
    }

    private void addStages(Project project, List<String> stageNames) {
        if (stageNames != null && !stageNames.isEmpty()) {
            List<Stage> stages = new ArrayList<>();
            for (String stageName : stageNames) {
                Stage stage = Stage.builder()
                        .project(project)
                        .stageName(stageName)
                        .build();
                stages.add(stageRepository.save(stage));
            }
            project.setStages(stages);
        }
    }

    private void addChildren(Project project, List<CreateSubProjectDto> childrenDtos) {
        if (childrenDtos != null && !childrenDtos.isEmpty()) {
            List<Project> updatedChildren = new ArrayList<>();
            for (CreateSubProjectDto child : childrenDtos) {
                child.setParentProject(project.getId());
                updatedChildren.add(projectMapper.toEntity(create(child)));
            }
            project.setChildren(updatedChildren);
        }
    }

    private void createMoment(Project project) {
        List<Project> momentProjects = new ArrayList<>(project.getChildren());
        momentProjects.add(project);
        Moment moment = Moment.builder()
                .name(project.getName())
                .description(MomentType.COMPLETED.getDescription())
                .projects(momentProjects)
                .date(LocalDateTime.now())
                .createdBy(project.getOwnerId())
                .build();
        momentRepository.save(moment);
    }

    private Project validateAndRetrieveParentProject(CreateSubProjectDto subProjectDto) {
        subProjectDto.validateCommonFields();

        var parentId = subProjectDto.getParentProject();
        Project parentProject = projectRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent project with id = " + parentId + " doesn't exist"));

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && subProjectDto.getVisibility() == ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException("Project " + subProjectDto.getName() + " cannot be public");
        }

        return parentProject;
    }
}
