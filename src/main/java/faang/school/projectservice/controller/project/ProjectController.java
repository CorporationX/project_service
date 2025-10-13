package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    public ProjectDto addProject(CreateProjectDto projectDto) {
        return projectService.create(userContext.getUserId(), projectDto);
    }

    public ProjectDto updateProject(long projectId, UpdateProjectDto projectDto) {
        return projectService.update(userContext.getUserId(), projectId, projectDto);
    }

    public ProjectDto getById(long projectId) {
        return projectService.getById(userContext.getUserId(), projectId);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAll(userContext.getUserId());
    }

}
