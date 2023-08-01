package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Builder
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final StageRepository stageRepository;
    private final MomentRepository momentRepository;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateSubProject(projectDto);
        validateProjectNotExist(projectDto);
        validateParentProjectExist(projectDto);
        checkSubProjectNotPrivateOnPublicProject(projectDto);
        Project subProject = subProjectMapper.toEntity(projectDto);
        subProject.setChildren(projectRepository.findAllByIds(projectDto.getChildrenIds()));
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        List<Stage> stages = projectDto.getStagesId().stream()
                .map(stageRepository::getById)
                .toList();
        subProject.setStages(stages);
        parentProject.getChildren().add(subProject);
        projectRepository.save(subProject);
        projectRepository.save(parentProject);
        return subProjectMapper.toDto(subProject);
    }

    public List<ProjectDto> createSubProjects(List<ProjectDto> projectsDtos) {
        projectsDtos.forEach(this::validateSubProject);
        return projectsDtos.stream()
                .map(this::createSubProject)
                .toList();
    }

    public Timestamp updateSubProject(ProjectDto projectDto) {
        Project updatedProject = subProjectMapper.toEntity(projectDto);
        Project originalProject = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getStatus() != null && projectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            List<Project> allProjects = projectRepository.findAllByIds(projectDto.getChildrenIds());
            allProjects.forEach(this::checkSubProjectStatusCompleteOrCancelled);
            updatedProject.setChildren(allProjects);
            updateAllNeededFields(projectDto, originalProject, updatedProject);
            projectRepository.save(updatedProject);
            momentRepository.save(createMoment(projectDto));
            return Timestamp.valueOf(originalProject.getUpdatedAt());
        }

        List<Project> subProjects = projectRepository.findAllByIds(projectDto.getChildrenIds());

        if (projectDto.getVisibility() != null && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            subProjects.stream()
                    .peek(subProject -> subProject.setVisibility(ProjectVisibility.PRIVATE))
                    .forEach(projectRepository::save);
            updatedProject.setChildren(subProjects);
        } else {
            updatedProject.setChildren(subProjects);
        }

        updateAllNeededFields(projectDto, originalProject, updatedProject);
        projectRepository.save(updatedProject);
        return Timestamp.valueOf(originalProject.getUpdatedAt());
    }

    public List<ProjectDto> getProjectChildrenWithFilter(ProjectFilterDto projectFilterDto, long projectId) {
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

    public Moment createMoment(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        Moment moment = Moment.builder()
                .name(String.format("%s project tasks", projectDto.getName()))
                .description(String.format("All tasks are completed in %s project", projectDto.getName()))
                .resource(project.getResources())
                .projects(new ArrayList<>(project.getChildren()))
                .userIds(collectAllUsersIdOnProject(project))
                .imageId(project.getCoverImageId())
                .createdBy(projectDto.getOwnerId())
                .updatedBy(project.getOwnerId())
                .build();
        moment.getProjects().add(project);
        return moment;
    }

    public List<Long> collectAllUsersIdOnProject(Project project) {
        Set<Long> userIds = new HashSet<>();
        if (project.getTeams() != null && !project.getTeams().isEmpty()) {
            if (project.getChildren() != null && !project.getChildren().isEmpty()) {
                collectTeamUserIds(project, userIds);
                collectChildrenMomentUserIds(project, userIds);
            } else {
                collectTeamUserIds(project, userIds);
            }
        } else if (project.getChildren() != null && !project.getChildren().isEmpty()) {
            collectChildrenMomentUserIds(project, userIds);
        }
        return new ArrayList<>(userIds);
    }

    public Project changeParentProject(ProjectDto projectDto, Project originalProject) {
        if (!Objects.equals(projectDto.getParentProjectId(), originalProject.getParentProject().getId())) {
            Project newParentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
            originalProject.setParentProject(newParentProject);
        }
        return originalProject;
    }

    private void updateAllNeededFields(ProjectDto projectDto, Project originalProject, Project updatedProject) {
        Project originalWithNewParrentProject = changeParentProject(projectDto, originalProject);
        updatedProject.setParentProject(originalWithNewParrentProject.getParentProject());
        setThreeField(projectDto, updatedProject);
    }

    private void setThreeField(ProjectDto projectDto, Project project) {
        if (projectDto.getStatus() != null) {
            project.setStatus(projectDto.getStatus());
        }
        if (projectDto.getStagesId() != null) {
            List<Stage> stages = projectDto.getStagesId().stream()
                    .map(stageRepository::getById)
                    .toList();
            project.setStages(stages);
        }
        if (projectDto.getVisibility() != null) {
            checkSubProjectNotPrivateOnPublicProject(projectDto);
            project.setVisibility(projectDto.getVisibility());
        }
    }

    private void collectTeamUserIds(Project project, Set<Long> collectIn) {
        project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .map(TeamMember::getId))
                .forEach(collectIn::add);
    }

    private void collectChildrenMomentUserIds(Project project, Set<Long> collectIn) {
        project.getChildren().stream()
                .flatMap(subProject -> momentRepository.findAllByProjectId(subProject.getId()).stream())
                .flatMap(subProjectMoment -> subProjectMoment.getUserIds().stream())
                .forEach(collectIn::add);
    }

    private void validateSubProject(ProjectDto projectDto) {
        if (projectDto.getOwnerId() <= 0) {
            throw new DataValidationException("Owner id cant be less then 1");
        }
        if (projectDto.getChildrenIds() == null) {
            throw new DataValidationException("Subprojects can be empty but not null");
        }
        if (projectDto.getStatus() == null) {
            throw new DataValidationException("Project status cant be null");
        }
        if (projectDto.getVisibility() == null) {
            throw new DataValidationException(String.format("Visibility of subProject '%s' must be specified as 'private' or 'public'.", projectDto.getName()));
        }
        if (projectDto.getParentProjectId() <= 0) {
            throw new DataValidationException("ParentProjectId cant be less 0 or 0");
        }
    }

    private void checkSubProjectNotPrivateOnPublicProject(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException(String.format("Private SubProject; %s, cant be with a public Parent Project: %s", projectDto.getName(), parentProject.getName()));
        }
    }

    private void validateParentProjectExist(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject == null) {
            throw new EntityNotFoundException(String.format("Parent project not found by id: %s", projectDto.getParentProjectId()));
        }
    }

    private void validateProjectNotExist(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format("Project %s is already exist", projectDto.getName()));
        }
    }

    private void checkSubProjectStatusCompleteOrCancelled(Project subProject) {
        if (subProject.getStatus() != ProjectStatus.COMPLETED && subProject.getStatus() != ProjectStatus.CANCELLED) {
            throw new DataValidationException("Can't close project if subProject status are not complete or cancelled");
        }
    }
}