package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.dto.project.GeneralProjectInfoDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.listeners.events.MomentEvent;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final StageRepository stageRepository;
    private final TeamRepository teamRepository;
    private final ProjectMapper projectMapper;
    private final SubProjectMapper subProjectMapper;
    private final List<Filter<FilterProjectDto, Project>> filters;
    private final ApplicationEventPublisher eventPublisher;

    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        setStagesIfNotNull(projectDto, project);
        setTeamsIfNotNull(projectDto, project);
        return projectMapper.toDto(projectJpaRepository.save(project));
    }

    public CreateSubProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto subProjectDto) {
        Project parentProject = projectJpaRepository.getReferenceById(parentProjectId);
        Project childProject = subProjectMapper.toEntity(subProjectDto);
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) &&
                childProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            log.warn("It's not possible to a create private subproject for a public project. Parent project id {} is {}< Child project id {} is {}"
                    , parentProjectId
                    , parentProject.getVisibility()
                    , childProject.getId()
                    , childProject.getVisibility()
            );
            throw new DataValidationException("It's not possible to a create private subproject for a public project");
        }
        log.info("Got child project from Dto withing creating subproject with id = {}", childProject.getId());
        childProject.setParentProject(parentProject);
        parentProject.getChildren().add(childProject);
        setStagesIfNotNull(subProjectDto, parentProject);
        setTeamsIfNotNull(subProjectDto, parentProject);
        projectJpaRepository.saveAll(List.of(parentProject, childProject));
        return subProjectMapper.toDto(childProject);
    }

    public CreateSubProjectDto updateProject(long id, CreateSubProjectDto dto, long userId) {
        Project project = projectJpaRepository.getReferenceById(id);
        List<Project> children = projectJpaRepository.getAllSubprojectsForProjectId(id);
        log.info("Got all subprojects for project with id = {}", id);
        List<Moment> moments = new ArrayList<>();
        dto.setId(id);
        setNameIfNotNull(project, dto);
        changeParentProjectIdIfDifferent(project, dto);
        updateStatusIfNotNull(project, dto, children, userId);
        updateVisibilityIfNotNull(project, dto, children);
        setStagesIfNotNull(dto, project);
        setTeamsIfNotNull(dto, project);
        return subProjectMapper.toDto(projectJpaRepository.save(project));
    }

    public List<CreateSubProjectDto> getProjectByFilters(FilterProjectDto filterDto, Long projectId) {
        Stream<Project> projectStream = projectJpaRepository.getAllSubprojectsForProjectId(projectId).stream();
        return filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(projectStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subStream, stream) -> stream))
                .distinct()
                .map(subProjectMapper::toDto)
                .toList();
    }

    private void setNameIfNotNull(Project project, CreateSubProjectDto dto) {
       if (dto.getName() == null || dto.getName().isEmpty()) {
           dto.setName(project.getName());
       }
        if (!dto.getName().isEmpty() || !dto.getName().isBlank() && !dto.getName().matches(project.getName())) {
            project.setName(dto.getName());
        }
    }

    private void changeParentProjectIdIfDifferent(Project project, CreateSubProjectDto dto) {
        if (dto.getParentProjectId() != null && !dto.getParentProjectId().equals(project.getId())) {
            Project recentParentProject = project.getParentProject();
            Optional<Project> supposedParentProject = projectJpaRepository.findById(dto.getParentProjectId());

            supposedParentProject.ifPresentOrElse(p -> {
                        List<Project> supposedList = new ArrayList<>();
                        if (p.getChildren() != null) {
                            supposedList.addAll(p.getChildren());
                        }
                        supposedList.add(project);
                        p.setChildren(supposedList);
                        if (recentParentProject != null) {
                            List<Project> recentList = new ArrayList<>(recentParentProject.getChildren());
                            recentList.remove(project);
                            recentParentProject.setChildren(recentList);
                        }
                        log.info("Replacing of the project id = {} to project id = {} was successfully finished", dto.getId(), p.getId());
                    }
                    , () -> {
                        log.warn("Couldn't find project in DB withing replacing project to another one");
                        supposedParentProject.orElseThrow(() -> new DataValidationException("No such project in DB"));
                    });
        }
    }

    private void updateVisibilityIfNotNull(Project project, CreateSubProjectDto dto, List<Project> children) {
        if (dto.getVisibility() != project.getVisibility()) {
            if (dto.getVisibility() == ProjectVisibility.PRIVATE) {
                project.setVisibility(dto.getVisibility());
                log.info("Set PRIVATE visibility to project id = {}", project.getId());
                if (!children.isEmpty()) {
                    children.forEach(p -> p.setVisibility(dto.getVisibility()));
                    projectJpaRepository.saveAll(children);
                    log.info("All children projects were saved with Private visibility");
                }
            } else {
                project.setVisibility(dto.getVisibility());
                log.info("Set {} visibility to project id = {}", dto.getVisibility(), project.getId());
            }
        }
    }

    private void updateStatusIfNotNull(Project project, CreateSubProjectDto dto, List<Project> children, Long userId) {
        if (dto.getStatus() != project.getStatus()) {
            if (dto.getStatus().equals(ProjectStatus.COMPLETED)) {
                List<Project> completedChildProjects = getCompletedChildProjects(children);
                log.info("Got list of completed projects");
                if (!completedChildProjects.isEmpty() && completedChildProjects.size() == children.size()) {
                    project.setStatus(dto.getStatus());
                    List<Moment> childrenMoments = children.stream().
                            map(Project::getMoments)
                            .flatMap(Collection::stream)
                            .toList();
                    assignMomentsToAccomplishedProject(project, userId, completedChildProjects);
//                    moments = addMomentToList(project.getId(), childrenMoments, userId);
//                    project.setMoments(moments);
//                    log.info("Moments from children projects was set successfully for project id = " + project.getId());
                } else if (completedChildProjects.isEmpty()) {
                    project.setStatus(dto.getStatus());
                    assignMomentsToAccomplishedProject(project, userId, new ArrayList<>());
//                    moments = addMomentToList(project.getId(), moments, userId);
//                    project.setMoments(moments);
//                    log.info("Moment was set successfully for project id = " + project.getId());
                } else {
                    log.error("Project id = {} has unfinished subprojects", project.getId());
                    throw new DataValidationException("current project has unfinished subprojects");
                }
            } else {
                project.setStatus(dto.getStatus());
                log.info("Set any status except for COMPLETED");
            }
        }
    }

//

    private List<Project> getCompletedChildProjects(List<Project> childProjects) {
        if (!childProjects.isEmpty()) {
            return childProjects.stream().
                    filter(p -> p.getStatus().equals(ProjectStatus.COMPLETED)).
                    toList();
        }
        return List.of();
    }

    private void setStagesIfNotNull(GeneralProjectInfoDto dto, Project project) {
        if (dto.getStagesIds() != null) {
            List<Stage> stageList = stageRepository.findAllById(dto.getStagesIds());
            project.setStages(stageList);
        }
    }

    private void setTeamsIfNotNull(GeneralProjectInfoDto dto, Project project) {
        if (dto.getTeamsIds() != null) {
            List<Team> teamList = teamRepository.findAllById(dto.getTeamsIds());
            project.setTeams(teamList);
        }
    }

    private void assignMomentsToAccomplishedProject(Project project, Long userId, List<Project> childProjects) {
        String message = String.format("Project with id = %s has been completed", project.getId());
        MomentEvent momentEvent = new MomentEvent(project, userId, childProjects, message);
        eventPublisher.publishEvent(momentEvent);
    }
}
