package faang.school.projectservice.controller.projectController;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponseDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @PutMapping("/{projectId}")
    public ProjectResponseDto updateProject(@Valid @Positive @PathVariable Long projectId,
                                            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(projectId, projectUpdateDto);
    }

    @PostMapping("/filtered")
    public List<ProjectResponseDto> getAllProjectsWithFilters(@Valid @RequestBody ProjectFilterDto filterDto) {
        return projectService.findAllProjectsWithFilters(filterDto);
    }

    @GetMapping("/all")
    public List<ProjectResponseDto> findAllProjects() {
        return projectService.findAllProject();
    }

    @GetMapping("/get/{projectId}")
    public ProjectResponseDto getProjectById(@Valid @Positive @PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }
}
