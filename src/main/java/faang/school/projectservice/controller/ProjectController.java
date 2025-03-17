package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/new")
    public void createProject(@RequestParam Long userId, @RequestBody ProjectDto projectDto) {
        projectService.createProject(userId, projectDto);
    }

    @PutMapping("/{projectId}")
    public void updateProject(@PathVariable Long projectId, @RequestBody ProjectDto projectDto) {
        projectService.updateProject(projectId, projectDto);
    }

    @PostMapping("/all-filtered")
    public List<ProjectDto> findProjectsByFilters(@RequestParam Long userId,
                                                  @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findProjectsByFilters(userId, projectFilterDto);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects(@RequestParam Long userId) {
        return projectService.getAllProjects(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@RequestParam Long userId, @PathVariable Long projectId) {
        return projectService.getProjectById(userId, projectId);
    }
}
