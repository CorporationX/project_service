package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ProjectController — описание класса.
 * <p>
 * - Создать эндпоинты для всех методов сервиса:
 * - POST /projects — создание проекта
 * - PUT /projects/{id} — обновление проекта
 * - GET /projects — получение проектов с фильтрами
 * - GET /projects/all — получение всех проектов
 * - GET /projects/{id} — получение проекта по ID
 * - DELETE /projects/{id} — удаление проекта
 * - Валидировать входящие данные (ProjectDto).
 * - Передавать вызовы в ProjectService.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 17.08.2025
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService service;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        ProjectDto createdProject = service.createProject(projectDto);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        ProjectDto updatedProject = service.updateProject(id, projectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilter(@RequestBody ProjectDto projectDto) {
        List<ProjectDto> projects = service.getProjectsByFilter(projectDto);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = service.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long projectId) {
        ProjectDto projectDto = service.getProjectById(projectId);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        service.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}