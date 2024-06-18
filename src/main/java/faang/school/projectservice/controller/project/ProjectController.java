package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto){
        return projectService.createProject(projectDto);
    }

    public ProjectDto updateProject(ProjectDto projectDto, ProjectStatus projectStatus, String description){
        return projectService.updateProject(projectDto, projectStatus, description);
    }

    public List<ProjectDto> getAllProjects(){
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable("id") long projectId) {
        return projectService.findProjectById(projectId);
    }

    public List<ProjectDto> findAllByFilter(ProjectFilterDto projectFilterDto, Long userId){
        return projectService.findAllByFilter(projectFilterDto, userId);
    }
}

