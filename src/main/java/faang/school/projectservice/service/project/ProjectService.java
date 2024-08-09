package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectMapper projectMapper;
    private final SubProjectMapper subProjectMapper;
    private final List<Filter<FilterProjectDto, Project>> filters;
    private final ProjectUtilService projectUtilService;
    private final ProjectValidateService projectValidateService;
    private final MomentService momentService;

    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        project.setStages(projectValidateService.getStages(projectDto));
        project.setTeams(projectValidateService.getTeams(projectDto));
        return projectMapper.toDto(projectUtilService.save(project));
    }

    public CreateSubProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto subProjectDto) {
        Project parentProject = projectUtilService.getProjectById(parentProjectId);
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

        parentProject.setStages(projectValidateService.getStages(subProjectDto));
        parentProject.setTeams(projectValidateService.getTeams(subProjectDto));
        projectUtilService.saveAllProjects(List.of(parentProject, childProject));
        return subProjectMapper.toDto(childProject);
    }

    public CreateSubProjectDto updateProject(long id, CreateSubProjectDto dto, long userId) {
        Project project = projectUtilService.getProjectById(id);
        List<Project> children = projectUtilService.getAllSubprojectsToProjectById(id);
        log.info("Got all subprojects for project with id = {}", id);
        project.setName(projectValidateService.getName(project.getName(), dto));
        if (projectValidateService.dtoParentProjectIdDoesNotMatchProjectIdAndNotNull(project.getId(), dto)) {
            changeParentProject(project, dto);
        }
        if (projectValidateService.visibilityDtoAndProjectDoesNotMatches(project.getVisibility(), dto)) {
            updateVisibility(project, dto, children);
        }
        if (projectValidateService.statusDtoAndProjectDoesNotMatches(project.getStatus(), dto)) {
            updateStatus(project, dto, children, userId);
        }
        project.setStages(projectValidateService.getStages(dto));
        project.setTeams(projectValidateService.getTeams(dto));
        return subProjectMapper.toDto(projectUtilService.save(project));
    }

    public List<CreateSubProjectDto> getProjectByFilters(FilterProjectDto filterDto, Long projectId) {
        Stream<Project> projectStream = projectUtilService.getAllSubprojectsToProjectById(projectId).stream();
        return filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(projectStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subStream, stream) -> stream))
                .distinct()
                .map(subProjectMapper::toDto)
                .toList();
    }

    private void changeParentProject(Project project, CreateSubProjectDto dto) {
        Project recentParentProject = project.getParentProject();
        Project supposedParentProject = projectUtilService.getProjectById(dto.getParentProjectId());
        project.setParentProject(supposedParentProject);
        List<Project> supposedList = new ArrayList<>();
        if (supposedParentProject.getChildren() != null) {
            supposedList.addAll(supposedParentProject.getChildren());
        }
        supposedList.add(project);
        supposedParentProject.setChildren(supposedList);
        if (recentParentProject != null) {
            List<Project> recentList = new ArrayList<>(recentParentProject.getChildren());
            recentList.remove(project);
            recentParentProject.setChildren(recentList);
        }
        log.info("Replacing of the project id = {} to project id = {} was successfully finished", project.getId(), supposedParentProject.getId());
    }

    private void updateVisibility(Project project, CreateSubProjectDto dto, List<Project> children) {
        if (dto.getVisibility() == ProjectVisibility.PRIVATE) {
            project.setVisibility(dto.getVisibility());
            log.info("Set PRIVATE visibility to project id = {}", project.getId());
            if (!children.isEmpty()) {
                children.forEach(p -> p.setVisibility(dto.getVisibility()));
                projectUtilService.saveAllProjects(children);
                log.info("All children projects were saved with Private visibility");
            }
        } else {
            project.setVisibility(dto.getVisibility());
            log.info("Set {} visibility to project id = {}", dto.getVisibility(), project.getId());
        }
    }

    private void updateStatus(Project project, CreateSubProjectDto dto, List<Project> children, Long userId) {
        if (dto.getStatus().equals(ProjectStatus.COMPLETED)) {
            List<Project> completedChildProjects = getCompletedChildProjects(children);
            log.info("Got list of completed projects");
            if (!completedChildProjects.isEmpty() && completedChildProjects.size() == children.size()) {
                project.setStatus(dto.getStatus());
                addMoment(project.getId(), completedChildProjects, userId);
            } else if (completedChildProjects.isEmpty()) {
                project.setStatus(dto.getStatus());
                addMoment(project.getId(), new ArrayList<>(), userId);
            } else {
                log.error("Project id = {} has unfinished subprojects", project.getId());
                throw new DataValidationException("current project has unfinished subprojects");
            }
        } else {
            project.setStatus(dto.getStatus());
            log.info("Set any status except for COMPLETED");
        }
    }

    private List<Project> getCompletedChildProjects(List<Project> childProjects) {
        if (!childProjects.isEmpty()) {
            return childProjects.stream().
                    filter(p -> p.getStatus().equals(ProjectStatus.COMPLETED)).
                    toList();
        }
        return List.of();
    }

    public ProjectDto getProjectById(long projectId) {
        Project project = projectUtilService.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        List<Project> projects = projectUtilService.getAllByIds(ids);
        return projects.stream().map(projectMapper::toDto).toList();
    }

    private void addMoment(Long projectId, List<Project> childProjects, Long userId) {
        String message = String.format("Project with id = %s has been completed", projectId);
        MomentRequestDto momentRequestDto = new MomentRequestDto();
        momentRequestDto.setName(message);
        List<Long> completedChildrenProjectsIds = childProjects.stream().map(Project::getId).toList();
        List<Long> completedProjects = new ArrayList<>();
        completedProjects.add(projectId);
        completedProjects.addAll(completedChildrenProjectsIds);
        momentRequestDto.setProjectIds(completedProjects);
        momentService.addNew(momentRequestDto, userId);
    }
}
