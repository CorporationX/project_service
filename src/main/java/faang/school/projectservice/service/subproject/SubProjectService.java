package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectService projectService;
    private final MomentService momentService;
    private final SubProjectValidator subProjectValidator;
    private final ProjectMapper projectMapper;
    private final MomentMapper momentMapper;
    private final List<SubprojectFilter> subprojectFilters;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        subProjectValidator.validateCreateProjectDto(projectDto);
        prepareProjectForCreate(projectDto);
        return projectService.createProject(projectDto);
    }

    private void prepareProjectForCreate(ProjectDto projectDto) {
        Project parentProject = projectMapper.toProject(projectService.getProjectById(projectDto.getParentProjectId()));
        ProjectVisibility parentVisibility = parentProject.getVisibility();

        if (projectDto.getVisibility() != null) {
            subProjectValidator.validateVisibility(projectDto.getVisibility(), parentVisibility);
        } else {
            projectDto.setVisibility(parentVisibility);
        }
    }

    public ProjectDto updateStatusSubProject(StatusSubprojectDto statusSubprojectDto) {
        ProjectDto projectDto = projectService.getProjectById(statusSubprojectDto.getProjectId());
        subProjectValidator.validateSubProjectStatus(projectDto, statusSubprojectDto.getStatus());
        ProjectStatus status = statusSubprojectDto.getStatus();

        updateStatusSubproject(projectDto, status);
        return projectService.updateProject(projectDto.getId(), projectDto);
    }

    private void updateStatusSubproject(ProjectDto projectDto, ProjectStatus status) {
        Project project = projectMapper.toProject(projectDto);
        if (status == ProjectStatus.COMPLETED &&
                project.getVisibility() != ProjectVisibility.PRIVATE) {
            momentService.createMoment(momentMapper.toMomentDto(project));
        }
        projectDto.setStatus(status);
    }

    public ProjectDto updateVisibilitySubProject(VisibilitySubprojectDto visibilitySubprojectDto) {
        ProjectDto projectDto = projectService.getProjectById(visibilitySubprojectDto.getProjectId());
        updateVisibilitySubProject(projectDto, visibilitySubprojectDto.getVisibility());
        return projectService.updateProject(projectDto.getId(), projectDto);
    }

    private void updateVisibilitySubProject(ProjectDto projectDto, ProjectVisibility visibility) {
        if (projectDto.getParentProjectId() != null) {
            ProjectDto parentProject = projectService.getProjectById(projectDto.getParentProjectId());
            subProjectValidator.validateVisibility(visibility, parentProject.getVisibility());
        }

        if (visibility == ProjectVisibility.PRIVATE) {
            changeAllVisibilityInSubproject(projectDto);
        }

        projectDto.setVisibility(visibility);
    }

    private void changeAllVisibilityInSubproject(ProjectDto projectDto) {
        Deque<ProjectDto> stack = new ArrayDeque<>();
        stack.push(projectDto);

        while (!stack.isEmpty()) {
            ProjectDto currentProject = stack.pop();
            currentProject.setVisibility(ProjectVisibility.PRIVATE);
            projectService.updateProject(currentProject.getId(), currentProject);
            if (currentProject.getChildrenIds() == null) {
                continue;
            }

            for (Long ids : currentProject.getChildrenIds()) {
                stack.push(projectService.getProjectById(ids));
            }
        }
    }

    public List<ProjectDto> getAllSubProject(SubprojectFilterDto filters) {
        ProjectDto projectDto = projectService.getProjectById(filters.getProjectId());
        Stream<Project> subprojects = projectDto.getChildrenIds().stream()
                .map(id -> projectMapper.toProject(projectService.getProjectById(id)))
                .toList()
                .stream();
        return filterSubProject(filters, subprojects);
    }

    private List<ProjectDto> filterSubProject(SubprojectFilterDto filters, Stream<Project> subprojects) {
        List<SubprojectFilter> stream = subprojectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        for (SubprojectFilter subprojectFilter : stream) {
            subprojects = subprojectFilter.apply(subprojects, filters);
        }
        return subprojects.map(projectMapper::toProjectDto)
                .toList();
    }
}
