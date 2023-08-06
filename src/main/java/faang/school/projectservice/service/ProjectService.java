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
import faang.school.projectservice.validator.SubProjectValidator;
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
    private final SubProjectValidator subProjectValidator;

    public SubProjectDto createSubProject(SubProjectDto projectDto) {
        subProjectValidator.validateSubProject(projectDto);
        subProjectValidator.validateProjectNotExist(projectDto);
        subProjectValidator.validateParentProjectExist(projectDto);
        subProjectValidator.checkSubProjectNotPrivateOnPublicProject(projectDto);
        Project subProject = subProjectMapper.toEntity(projectDto);
        if (projectDto.getChildrenIds() != null && !projectDto.getChildrenIds().isEmpty()) {
            subProject.setChildren(projectRepository.findAllByIds(projectDto.getChildrenIds()));
        }
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        if (projectDto.getStagesId() != null && !projectDto.getStagesId().isEmpty()) {
            List<Stage> stages = projectDto.getStagesId().stream()
                    .map(stageRepository::getById)
                    .toList();
            subProject.setStages(stages);
        }
        parentProject.getChildren().add(subProject);
        projectRepository.save(subProject);
        projectRepository.save(parentProject);
        return subProjectMapper.toDto(subProject);
    }

    public List<SubProjectDto> createSubProjects(List<SubProjectDto> projectsDtos) {
        if (projectsDtos.isEmpty()) {
            throw new DataValidationException("List of project is empty");
        }
        projectsDtos.forEach(subProjectValidator::validateSubProject);
        return projectsDtos.stream()
                .map(this::createSubProject)
                .toList();
    }

    public Timestamp updateSubProject(SubProjectDto projectDto) {
        Project projectToUpdate = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getStatus() != null && projectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            if (projectDto.getChildrenIds() != null && !projectDto.getChildrenIds().isEmpty()) {
                List<Project> allProjects = projectRepository.findAllByIds(projectDto.getChildrenIds());
                allProjects.forEach(subProjectValidator::checkSubProjectStatusCompleteOrCancelled);
                projectToUpdate.setChildren(allProjects);
            }
            updateAllNeededFields(projectDto, projectToUpdate);
            projectRepository.save(projectToUpdate);
            Moment projectMoment = createMoment(projectDto, projectToUpdate);
            momentRepository.save(projectMoment);
            return Timestamp.valueOf(projectToUpdate.getUpdatedAt());
        }
        if (projectDto.getChildrenIds() != null && !projectDto.getChildrenIds().isEmpty()) {
            List<Project> subProjects = projectRepository.findAllByIds(projectDto.getChildrenIds());

            if (projectDto.getVisibility() != null && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                subProjects.forEach(subProject -> {
                    subProject.setVisibility(ProjectVisibility.PRIVATE);
                    projectRepository.save(subProject);
                    projectToUpdate.setChildren(subProjects);
                });
            }
        }
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
            subProjectValidator.checkSubProjectNotPrivateOnPublicProject(projectDto);
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
}