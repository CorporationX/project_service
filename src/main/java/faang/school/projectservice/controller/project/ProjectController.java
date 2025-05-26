package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    ProjectService projectService;

    public ProjectDto createProject(
            @PathVariable long userId,
            @RequestBody ProjectDto projectDto) {
        return projectService.createProject(userId, projectDto);
    }

    public ProjectDto updateProject(
            @PathVariable long userId,
            @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(userId, projectDto);
    }

    public List<ProjectDto> getProjectsByStatus(
            @PathVariable ProjectStatus projectStatus) {
        return projectService.getProjectsByStatus(projectStatus);
    }
}
