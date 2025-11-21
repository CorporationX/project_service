package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    public ProjectDto addProject(@Valid CreateProjectDto projectDto) {
        return projectService.create(userContext.getUserId(), projectDto);
    }

    public ProjectDto updateProject(long projectId, @Valid UpdateProjectDto projectDto) {
        return projectService.update(userContext.getUserId(), projectId, projectDto);
    }

    public ProjectDto getById(long projectId) {
        return projectService.getById(userContext.getUserId(), projectId);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAll(userContext.getUserId());
    }

    public List<ProjectDto> search(@Valid String name, @Valid ProjectStatus status) {
        return projectService.search(userContext.getUserId(), name, status);
    }
    
}
