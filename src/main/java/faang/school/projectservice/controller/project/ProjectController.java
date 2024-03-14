package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoint for managing projects")
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @PostMapping("/filter/{userId}")
    public List<ProjectDto> findProjectByFilters(@PathVariable("userId") Long userId, @RequestBody ProjectDtoFilter projectDtoFilter) {
        return projectService.findProjectByFilters(userId, projectDtoFilter);
    }

    @GetMapping("/all")
    public List<ProjectDto> findAllProjects() {
        return projectService.findAllProjects();
    }

    @GetMapping("/{projectId}")
    public ProjectDto findProjectById(@PathVariable("projectId") Long projectId){
        return projectService.findProjectById(projectId);
    }
}
