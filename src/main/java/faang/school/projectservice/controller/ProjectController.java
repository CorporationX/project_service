package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectCreateResponseDto createProject(@RequestBody ProjectCreateRequestDto projectCreateRequestDto) {
        return projectService.createProject(projectCreateRequestDto);
    }

    @PutMapping
    public ProjectUpdateResponseDto updateProject(@RequestBody ProjectUpdateRequestDto projectUpdateRequestDto) {
        return projectService.updateProject(projectUpdateRequestDto);
    }

    @GetMapping("/users/{userId}")
    public List<ProjectResponseDto> getAllVisibleProjects(@PathVariable Long userId, ProjectFilterDto filters) {
        return projectService.getAllVisibleProjects(userId, filters);
    }

    @GetMapping
    public List<ProjectResponseDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponseDto getProjectById(@PathVariable Long id) {
        return projectService.getProjectDtoById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
        projectService.deleteProjectById(id);
        return ResponseEntity.ok().build();
    }
}
