package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.CreateSubProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final MomentRepository momentRepository;
    private final StageRepository stageRepository;
    private final CreateSubProjectMapper createdSubProjectMapper;
    private final SubProjectMapper subProjectMapper;
    private final List<SubProjectFilter> subProjectFilters;

    public SubProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        Project parentProject = validateAndRetrieveParentProject(subProjectDto);
        Project subProject = createdSubProjectMapper.toEntity(subProjectDto);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        Project savedSubProject = projectRepository.save(subProject);

        addStages(savedSubProject, subProjectDto.getStages());
        addChildren(savedSubProject, subProjectDto.getChildren());

        SubProjectDto savedDto = subProjectMapper.toDto(projectRepository.save(savedSubProject));
        log.info("Subproject with name = {} and id = {} was created", savedDto.getName(), savedDto.getId());

        return subProjectMapper.toDto(projectRepository.save(savedSubProject));
    }

    public SubProjectDto updateSubProject(SubProjectDto subProjectDto) {
        subProjectDto.validateCommonFields();

        Project project = projectRepository.findById(subProjectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project with id = %d doesn't exist", subProjectDto.getId())));
        ProjectStatus status = subProjectDto.getStatus();
        ProjectVisibility visibility = subProjectDto.getVisibility();

        processSubProjects(project, status, visibility);

        project.setVisibility(visibility);
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        SubProjectDto savedDto = subProjectMapper.toDto(projectRepository.save(project));

        log.info("Subproject with name = {} and id = {} was updated", savedDto.getName(), savedDto.getId());

        return savedDto;
    }

    private void processSubProjects(Project project, ProjectStatus status, ProjectVisibility visibility) {
        if (project.getChildren() != null && !project.getChildren().isEmpty()) {
            List<Project> subProjects = new ArrayList<>(project.getChildren());

            validateAllSubProjectsCompleted(project, subProjects, status);
            updateSubProjectVisibility(project, subProjects, visibility);
        }
    }

    private void validateAllSubProjectsCompleted(Project project, List<Project> subProjects, ProjectStatus status) {
        if (status == ProjectStatus.COMPLETED) {
            boolean areAllCompleted = subProjects.stream()
                    .allMatch(subProject -> subProject.getStatus() == ProjectStatus.COMPLETED);

            if (areAllCompleted) {
                createMoment(project);
            } else {
                throw new IllegalArgumentException("Not all subprojects are completed");
            }
        }
    }

    private void updateSubProjectVisibility(Project project, List<Project> subProjects, ProjectVisibility visibility) {
        boolean isAnyPublic = subProjects.stream()
                .anyMatch(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC);

        if (visibility == ProjectVisibility.PRIVATE && isAnyPublic) {
            subProjects.stream()
                    .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC)
                    .forEach(subProject -> {
                        subProject.setVisibility(ProjectVisibility.PRIVATE);
                        projectRepository.save(subProject);
                    });
            project.setChildren(subProjects);
        }
    }

    public List<SubProjectDto> getSubProjects(SubProjectsFilterDto filter) {
        var projectId = filter.projectId();
        if (projectId == null) {
            throw new IllegalArgumentException("Project id is required");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project with id = %d doesn't exist", projectId)));

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
                .map(subProjectMapper::toDto)
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
                updatedChildren.add(subProjectMapper.toEntity(createSubProject(child)));
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("Parent project with id = %d doesn't exist", parentId)));

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && subProjectDto.getVisibility() == ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException(String.format("Project %s cannot be public", subProjectDto.getName()));
        }

        return parentProject;
    }
}
