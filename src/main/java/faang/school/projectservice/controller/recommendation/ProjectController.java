package faang.school.projectservice.controller.recommendation;

import faang.school.projectservice.config.feign.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.ProjectManagementService;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectManagementService projectManagementService;
    private final UserContext userContext;

    @PostMapping
    public ProjectReadDto createProject(
            @Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectManagementService.createProject(projectCreateDto, userContext.getUserId());
    }

    @PutMapping("/{projectId}")
    public ProjectReadDto updateProject(
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto,
            @PathVariable long projectId) {
        return projectManagementService.updateProject(projectUpdateDto, projectId, userContext.getUserId());
    }

    @GetMapping
    public List<ProjectReadDto> getAllProjectsWithFilters(
            ProjectFilterDto filterDto) {
        return projectManagementService.getAllProjects(filterDto, userContext.getUserId());
    }

    @GetMapping("/{projectId}")
    public ProjectReadDto getProjectById(
            @PathVariable long projectId) {
        return projectManagementService.getProjectById(projectId, userContext.getUserId());
    }

    @PostMapping("/{projectId}/presentation")
    public ProjectReadDto generatePdf(@PathVariable long projectId) {
        return projectService.generateProjectPresentation(projectId);
    }

    @GetMapping("/{projectId}/presentation")
    public String getPresentationFilePath(@PathVariable long projectId) {
        return projectService.getPresentationFileKey(projectId);
    }

}
