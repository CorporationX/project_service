package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public ProjectDto createProject(ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    public List<ProjectDto> getProjectsByNameAndStatus(ProjectFilterDto filterDto) {
        return projectService.getProjectByNameAndStatus(filterDto);
    }

    public List<ProjectDto> getAllProject() {
        return projectService.getAllProject();
    }

    public ProjectDto getProject(Long id) {
        return projectService.getProject(id);
    }
}
