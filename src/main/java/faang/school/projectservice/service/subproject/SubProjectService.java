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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
