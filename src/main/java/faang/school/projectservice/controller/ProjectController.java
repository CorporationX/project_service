package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.s3.ProjectCoverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;
    private final ProjectCoverService coverService;

    @PostMapping
    public ResponseEntity<ProjectViewDto> createProject(@RequestBody @Valid ProjectCreateDto projectDto) {
        return ResponseEntity.ok(service.createProject(projectDto));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectViewDto> updateProject(@PathVariable long projectId,
                                                        @RequestBody @Valid ProjectUpdateDto projectDto) {
        return ResponseEntity.ok(service.updateProject(projectId, projectDto));
    }

    @GetMapping
    public ResponseEntity<List<ProjectViewDto>> getProjectByFilters(ProjectFilterDto projectFilterDto) {
        return ResponseEntity.ok(service.getByFilters(projectFilterDto));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectViewDto> getProjectById(@PathVariable long projectId) {
        return ResponseEntity.ok(service.getProjectById(projectId));
    }

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<ProjectViewDto> linkCover(@PathVariable long projectId,
                                                   @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(coverService.linkCover(projectId, file));
    }
}
