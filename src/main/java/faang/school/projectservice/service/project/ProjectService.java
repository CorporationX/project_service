package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectValidator;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
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
    private final List<Filter<FilterDto, Project>> filters;

    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        checkDtoForNullFields(projectDto, project);
        return projectMapper.toDto(projectJpaRepository.save(project));
    }

    public CreateSubProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto subProjectDto) {
        Project parentProject = projectJpaRepository.getReferenceById(parentProjectId);
        Project childProject = subProjectMapper.toEntity(subProjectDto);
        log.info("Got child project from Dto withing creating subproject with id = " + childProject.getId());
        childProject.setParentProject(parentProject);
        if (parentProject.getId() != null) {
            parentProject.getChildren().add(childProject);
            checkDtoForNullFields(subProjectDto, parentProject);
            if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) &&
                    childProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                log.warn("It's not possible to a create private subproject for a public project");
                throw new DataValidationException("It's not possible to a create private subproject for a public project");
            }
        }
        projectJpaRepository.save(parentProject);
        return subProjectMapper.toDto(projectJpaRepository.save(childProject));
    }

    public CreateSubProjectDto updateProject(long id, CreateSubProjectDto dto, long userId) {
        Project project = projectJpaRepository.getReferenceById(id);
        List<Project> children = projectJpaRepository.getAllSubprojectsFor(id);
        log.info("Got all subprojects for project with id = " + id);
        List<Moment> moments = new ArrayList<>();
        dto.setId(id);
        validateSetGeneralFields(project, dto);
        updateStatus(project, dto, children, moments, userId);
        updateVisibility(project, dto, children);
        checkDtoForNullFields(dto, project);
        return subProjectMapper.toDto(projectJpaRepository.save(project));
    }

    public List<CreateSubProjectDto> getProjectByFilters(FilterDto filterDto, Long projectId) {
        Stream<Project> projectStream = projectJpaRepository.getAllSubprojectsFor(projectId).stream();
        return filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(projectStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subStream, stream) -> stream))
                .distinct()
                .map(subProjectMapper::toDto)
                .toList();
    }

    private void validateSetGeneralFields(Project project, CreateSubProjectDto dto) {
        if (dto.getId() != null || !dto.getId().equals(project.getId())) {
            project.setId(dto.getId());
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            dto.setName(project.getName());
        }
        if (!dto.getName().isEmpty() || !dto.getName().isBlank() && !dto.getName().matches(project.getName())) {
            project.setName(dto.getName());
        } else {
            dto.setName(project.getName());
        }
        if (dto.getParentProjectId() != null && !dto.getParentProjectId().equals(project.getId())) {
            Project recentParentProject = project.getParentProject();
            Optional<Project> supposedParentProject = projectJpaRepository.findById(dto.getParentProjectId());

            supposedParentProject.ifPresentOrElse(p -> {
                        List<Project> supposedList = new ArrayList<>();
                        if (supposedParentProject.get().getChildren() == null) {
                            supposedList.add(project);
                            p.setChildren(supposedList);
                        } else {
                            supposedList.addAll(p.getChildren());
                            supposedList.add(project);
                            p.setChildren(supposedList);
                        }
                        if (recentParentProject != null) {
                            List<Project> recentList = new ArrayList<>(recentParentProject.getChildren());
                            recentList.remove(project);
                            recentParentProject.setChildren(recentList);
                        }
                        log.info("Replacing of the project id = " + dto.getId() + " to project id = "
                                + supposedParentProject.get().getId() + " was successfully finished");
                    }
                    , () -> {
                        log.warn("Couldn't find project in DB withing replacing project to another one");
                        supposedParentProject.orElseThrow(() -> new DataValidationException("No such project in DB"));
                    });
        }
    }

    private void updateVisibility(Project project, CreateSubProjectDto dto, List<Project> children) {
        if (dto.getVisibility() != project.getVisibility()) {
            if (dto.getVisibility() == ProjectVisibility.PRIVATE) {
                project.setVisibility(dto.getVisibility());
                log.info("Set PRIVATE visibility to project id = " + project.getId());
                if (!children.isEmpty()) {
                    children.forEach(p -> p.setVisibility(dto.getVisibility()));
                    projectJpaRepository.saveAll(children);
                    log.info("All children projects were saved with Private visibility");
                }
            } else {
                project.setVisibility(dto.getVisibility());
                log.info("Set " + dto.getVisibility() + " visibility to project id = " + project.getId());
            }
        }
    }

    private void updateStatus(Project project, CreateSubProjectDto dto, List<Project> children, List<Moment> moments, Long userId) {
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
                    moments = addMomentToList(project.getId(), childrenMoments, userId);
                    project.setMoments(moments);
                    log.info("Moments from children projects was set successfully for project id = " + project.getId());
                } else if (completedChildProjects.isEmpty()) {
                    project.setStatus(dto.getStatus());
                    moments = addMomentToList(project.getId(), moments, userId);
                    project.setMoments(moments);
                    log.info("Moment was set successfully for project id = " + project.getId());
                } else {
                    log.error("Project id = " + project.getId() + " has unfinished subprojects");
                    throw new DataValidationException("current project has unfinished subprojects");
                }
            } else {
                project.setStatus(dto.getStatus());
                log.info("Set any status except for COMPLETED");
            }
        }
    }

    private List<Moment> addMomentToList(long id, List<Moment> moments, long userId) {
        Moment moment = Moment.builder()
                .name("project " + id + " has been completed")
                .updatedBy(userId)
                .createdBy(userId)
                .build();
        List<Moment> list = new ArrayList<>(moments);
        list.add(moment);
        return list;
    }

    private List<Project> getCompletedChildProjects(List<Project> childProjects) {
        if (!childProjects.isEmpty()) {
            return childProjects.stream().
                    filter(p -> p.getStatus().equals(ProjectStatus.COMPLETED)).
                    toList();
        }
        return List.of();
    }

    private void checkDtoForNullFields(ProjectValidator dto, Project project) {
        if (dto.getStagesIds() != null) {
            List<Stage> stageList = stageRepository.findAllById(dto.getStagesIds());
            project.setStages(stageList);
        }
        if (dto.getTeamsIds() != null) {
            List<Team> teamList = teamRepository.findAllById(dto.getTeamsIds());
            project.setTeams(teamList);
        }
    }
}
