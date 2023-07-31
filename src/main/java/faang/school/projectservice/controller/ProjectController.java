package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/createProject")
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto changeStatus(@RequestBody ProjectDto projectDto, Long id) {
        return projectService.updateStatusAndDescription(projectDto, id);
    }

    @PostMapping("/{userId}")
    public List<ProjectDto> getProjectsByNameAndStatus(@RequestBody ProjectFilterDto projectFilterDto, @PathVariable long userId) {
        return projectService.getProjectByNameAndStatus(projectFilterDto, userId);
    }

    @GetMapping("/getProjectsByNameAndStatus")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProject();
    }

    @DeleteMapping("/deleteProjectById/{userId}")
    public void deleteProjectById(@PathVariable long id) {
        projectService.deleteProjectById(id);
    }

    @GetMapping("/getProjectById/{userId}")
    public ProjectDto getProjectById(@PathVariable long userId) {
        return projectService.getProjectById(userId);
    }
}
