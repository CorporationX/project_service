package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public ResponseEntity<ProjectDto> createProject(ProjectDto projectDto) {
        ProjectDto createdProject = projectService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }

    public ResponseEntity<ProjectDto> updateProject(long id, ProjectDto projectDto) {
        ProjectDto updatedProject = projectService.updateProject(id, projectDto);
        return ResponseEntity.ok(updatedProject);
    }

    public ResponseEntity<List<ProjectDto>> getAllProjectsByFilter(String name, ProjectStatus projectStatus) {
        List<ProjectDto> projects = projectService.getAllProjectsByFilter(name, projectStatus);
        return ResponseEntity.ok(projects);
    }

    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    public ResponseEntity<ProjectDto> findProjectById(long id) {
        ProjectDto projectDto = projectService.findProjectById(id);
        return ResponseEntity.ok(projectDto);
    }
}
