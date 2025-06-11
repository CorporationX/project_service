package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        Project createdProject = projectService.createProject(project);
        ProjectDto createdProjectDto = projectMapper.toDto(createdProject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProjectDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDto ProjectDto) {

        Project project = projectMapper.toEntity(ProjectDto);
        Project updatedProject = projectService.updateProject(id, project);

        return ResponseEntity.ok(projectMapper.toDto(updatedProject));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);

        return ResponseEntity.ok(projectMapper.toDto(project));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjects(String name, ProjectStatus status) {
        List<Project> projects = projectService.getProjectFilter(name, status);
        return ResponseEntity.ok(projectMapper.toDtoList(projects));
    }
}