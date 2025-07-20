package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<ProjectViewDto> createProject(@RequestParam @Valid ProjectCreateDto projectDto) {
        service.createProject(projectDto);
        return ResponseEntity.ok(service.createProject(projectDto));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectViewDto> updateProject(@PathVariable long projectId,
                                                        @RequestParam @Valid ProjectUpdateDto projectDto) {

        return ResponseEntity.ok(service.updateProject(projectId, projectDto));
    }

    @GetMapping("/projectsByStatus")
    public ResponseEntity<List<ProjectViewDto>> getProjectsFilteredByStatus(
            @RequestParam ProjectViewDto projectViewDto) {
        return ResponseEntity.ok(service.getProjectsFilteredByStatus(projectViewDto));
    }

    @GetMapping("/projectsByName")
    public ResponseEntity<List<ProjectViewDto>> getProjectsFilteredByName() {
        return ResponseEntity.ok(service.getProjectsFilteredByName());
    }

    @GetMapping
    public ResponseEntity<List<ProjectViewDto>> getAllProjects() {
        return ResponseEntity.ok(service.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectViewDto> getProjectById(@PathVariable long projectId) {
        return ResponseEntity.ok(service.getProjectById(projectId));
    }
}
