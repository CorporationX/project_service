package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.SubProjectDto;
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

    public SubProjectDto createSubProject(SubProjectDto projectDto) {
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

    public List<SubProjectDto> createSubProjects(List<SubProjectDto> projectsDtos) {
        projectsDtos.forEach(this::validateSubProject);
        return projectsDtos.stream()
                .map(this::createSubProject)
                .toList();
    }

    public Timestamp updateSubProject(SubProjectDto projectDto) {
        Project projectToUpdate = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getStatus() != null && projectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            List<Project> allProjects = projectRepository.findAllByIds(projectDto.getChildrenIds());
            allProjects.forEach(this::checkSubProjectStatusCompleteOrCancelled);
            projectToUpdate.setChildren(allProjects);
            updateAllNeededFields(projectDto, projectToUpdate);
            projectRepository.save(projectToUpdate);
            Moment projectMoment = createMoment(projectDto, projectToUpdate);
            momentRepository.save(projectMoment);
            return Timestamp.valueOf(projectToUpdate.getUpdatedAt());
        }

        List<Project> subProjects = projectRepository.findAllByIds(projectDto.getChildrenIds());

        if (projectDto.getVisibility() != null && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            subProjects.forEach(subProject -> {
                subProject.setVisibility(ProjectVisibility.PRIVATE);
                projectRepository.save(subProject);
            });
        }
        projectToUpdate.setChildren(subProjects);
        updateAllNeededFields(projectDto, projectToUpdate);
        projectRepository.save(projectToUpdate);
        return Timestamp.valueOf(projectToUpdate.getUpdatedAt());
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

    public Moment createMoment(SubProjectDto projectDto, Project project) {
        Moment moment = Moment.builder()
                .name(projectDto.getName() + " project tasks")
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

    public Project changeParentProject(SubProjectDto projectDto, Project projectToUpdate) {
        if (!Objects.equals(projectDto.getParentProjectId(), projectToUpdate.getParentProject().getId())) {
            Project newParentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
            projectToUpdate.setParentProject(newParentProject);
        }
        return projectToUpdate;
    }

    private void updateAllNeededFields(SubProjectDto projectDto, Project projectToUpdate) {
        changeParentProject(projectDto, projectToUpdate);
        setAllNeededFields(projectDto, projectToUpdate);
    }

    private void setAllNeededFields(SubProjectDto projectDto, Project projectToUpdate) {
        projectToUpdate.setName(projectToUpdate.getName());

        if (projectDto.getDescription() != null) {
            projectToUpdate.setDescription(projectDto.getDescription());
        }
        if (projectDto.getOwnerId() <= 0) {
            throw new DataValidationException("Owner id cant be less then 1");
        }

        projectToUpdate.setOwnerId(projectDto.getOwnerId());

        if (projectDto.getStatus() != null) {
            projectToUpdate.setStatus(projectDto.getStatus());
        }
        if (projectDto.getStagesId() != null) {
            List<Stage> stages = projectDto.getStagesId().stream()
                    .map(stageRepository::getById)
                    .toList();
            projectToUpdate.setStages(stages);
        }
        if (projectDto.getVisibility() != null) {
            checkSubProjectNotPrivateOnPublicProject(projectDto);
            projectToUpdate.setVisibility(projectDto.getVisibility());
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

    private void validateSubProject(SubProjectDto projectDto) {
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

    private void checkSubProjectNotPrivateOnPublicProject(SubProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException(String.format("Private SubProject; %s, cant be with a public Parent Project: %s", projectDto.getName(), parentProject.getName()));
        }
    }

    private void validateParentProjectExist(SubProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject == null) {
            throw new EntityNotFoundException(String.format("Parent project not found by id: %s", projectDto.getParentProjectId()));
        }
    }

    private void validateProjectNotExist(SubProjectDto projectDto) {
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