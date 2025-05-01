package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ImageService;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ImageService imageService;
    private final UserContext userContext;

    @PostMapping("/new")
    public void createProject(@RequestBody ProjectDto projectDto) {
        Long userId = userContext.getUserId();
        projectService.createProject(userId, projectDto);
    }

    @PutMapping("/{projectId}")
    public void updateProject(@PathVariable Long projectId, @RequestBody ProjectDto projectDto) {
        projectService.updateProject(projectId, projectDto);
    }

    @PostMapping("/all-filtered")
    public List<ProjectDto> findProjectsByFilters(@RequestBody ProjectFilterDto projectFilterDto) {
        Long userId = userContext.getUserId();
        return projectService.findProjectsByFilters(userId, projectFilterDto);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        Long userId = userContext.getUserId();
        return projectService.getAllProjects(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        Long userId = userContext.getUserId();
        return projectService.getProjectById(userId, projectId);
    }

    @PostMapping("/cover/{id}")
    public ResourceDto addCover(@PathVariable long id, @RequestBody MultipartFile file) throws IOException {
        return imageService.saveProjectCover(id, file);
    }
}
