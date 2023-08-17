package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectService projectService;
    private final MomentService momentService;
    private final SubProjectValidator subProjectValidator;
    private final ProjectMapper projectMapper;
    private final MomentMapper momentMapper;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        subProjectValidator.validateCreateProjectDto(projectDto);
        prepareProjectForCreate(projectDto);
        return projectService.createProject(projectDto);
    }

    public ProjectDto updateStatusSubProject(StatusSubprojectDto statusSubprojectDto) {
        subProjectValidator.validateSubProjectStatus(statusSubprojectDto.getId());

        Project project = projectService.getProjectById(statusSubprojectDto.getId());
        ProjectStatus status = statusSubprojectDto.getStatus();

        updateStatusSubproject(project, status);
        return projectMapper.toProjectDto(project);
    }

    public void updateVisibilitySubProject(VisibilitySubprojectDto updateStatusSubprojectDto) {
        Project project = projectService.getProjectById(updateStatusSubprojectDto.getId());
        Project parentProject = project.getParentProject();
        ProjectVisibility visibility = updateStatusSubprojectDto.getVisibility();

        updateVisibilitySubProject(project, parentProject, visibility);
    }

    private void prepareProjectForCreate(ProjectDto projectDto) {
        Project parentProject = projectService.getProjectById(projectDto.getParentProjectId());
        ProjectVisibility parentVisibility = parentProject.getVisibility();

        if (projectDto.getVisibility()!=null){
            subProjectValidator.validateVisibility(projectDto.getVisibility(), parentVisibility);
        } else {
            projectDto.setVisibility(parentVisibility);
        }
    }

    private void updateStatusSubproject(Project project, ProjectStatus status) {
        if (status == ProjectStatus.COMPLETED &&
                project.getVisibility() != ProjectVisibility.PRIVATE) {
            momentService.createMoment(momentMapper.toMomentDto(project));
        }

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
    }

    private void updateVisibilitySubProject(Project project, Project parentProject, ProjectVisibility visibility) {
        subProjectValidator.validateVisibility(visibility, parentProject.getVisibility());

        if (visibility == ProjectVisibility.PRIVATE) {
            changeAllVisibilityInSubproject(project);
        }

        project.setUpdatedAt(LocalDateTime.now());
        project.setVisibility(visibility);
    }

    private void changeAllVisibilityInSubproject(Project project) {
        Deque<Project> stack = new ArrayDeque<>();
        stack.push(project);

        while (!stack.isEmpty()) {
            Project currentProject = stack.pop();
            currentProject.setVisibility(ProjectVisibility.PRIVATE);

            if (currentProject.getChildren() == null) {
                continue;
            }

            for (Project subproject : currentProject.getChildren()) {
                stack.push(subproject);
            }
        }
    }
}
