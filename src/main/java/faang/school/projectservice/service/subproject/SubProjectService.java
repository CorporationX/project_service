package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
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

    public ProjectDto updateStatusSubProject(StatusSubprojectDto statusSubprojectDto) {
        subProjectValidator.validateSubProjectStatus(statusSubprojectDto.getId());

        Project project = projectService.getProjectById(statusSubprojectDto.getId());
        ProjectStatus status = statusSubprojectDto.getStatus();

        updateDataSubproject(project, status);
        return projectMapper.toProjectDto(project);
    }

    private void updateDataSubproject(Project project, ProjectStatus status) {
        if (status == ProjectStatus.COMPLETED &&
                project.getVisibility() != ProjectVisibility.PRIVATE) {
            momentService.createMoment(momentMapper.toMomentDto(project));
        }

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
    }

    public void updateVisibilitySubProject(VisibilitySubprojectUpdateDto updateStatusSubprojectDto) {
        Project project = projectService.getProjectById(updateStatusSubprojectDto.getId());
        Project parentProject = project.getParentProject();
        ProjectVisibility visibility = updateStatusSubprojectDto.getVisibility();

        updateSubProjectVisibility(project,parentProject, visibility);
    }

    private void updateSubProjectVisibility(Project project,Project parentProject, ProjectVisibility visibility) {
        if (visibility == ProjectVisibility.PUBLIC && parentProject.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new DataValidationException("You can't make public project in private project");
        }

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
