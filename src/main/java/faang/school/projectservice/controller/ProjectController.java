package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects(userContext.getUserId());
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable("id") long projectId) {
        return projectService.getProject(projectId);
    }

    @PostMapping("/create")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long projectId) {
        return projectService.updateProject(projectDto, projectId);
    }

    @PostMapping
    public List<ProjectDto> getProjects(@RequestBody ProjectFilterDto projectFilter) {
        return projectService.getProjects(projectFilter);
    }

    @PostMapping("/subproject")
    public ProjectDto createSubProject(@RequestBody ProjectDto projectDto) {
        validateSubProject(projectDto);
        return projectService.createSubProject(projectDto);
    }

    private void validateSubProject(ProjectDto projectDto) {
        if (projectDto.getName().isBlank()) {
            throw new DataValidException("Enter project name, please");
        }
    }
}