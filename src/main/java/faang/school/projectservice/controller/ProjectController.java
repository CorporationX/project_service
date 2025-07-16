package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.project.CreateProjectDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.UpdateProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService service;

    @PostMapping("/create")
    public ResponseEntity<Void> createProject(@RequestParam CreateProjectDto projectDto) {
        service.createProject(projectDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{projectId}/update")
    public ResponseEntity<Void> updateProject(@PathVariable long projectId,
                                              @RequestParam UpdateProjectDto projectDto) {
        service.updateProject(projectId, projectDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projectsByStatus")
    public ResponseEntity<List<ProjectDto>> getProjectsFilteredByStatus(
            @RequestParam ProjectDto projectDto) {
        return ResponseEntity.ok(service.getProjectsFilteredByStatus(projectDto));
    }

    @GetMapping("/projectsByName")
    public ResponseEntity<List<ProjectDto>> getProjectsFilteredByName() {
        return ResponseEntity.ok(service.getProjectsFilteredByName());
    }

    @GetMapping()
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(service.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable long projectId) {
        return ResponseEntity.ok(service.getProjectById(projectId));
    }
}
