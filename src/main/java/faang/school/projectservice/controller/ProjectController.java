package faang.school.projectservice.controller;

import faang.school.projectservice.controller.facade.project.ProjectFacade;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectFacade projectFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@RequestHeader("x-user-id") Long ownerId,
                                    @Valid @RequestBody ProjectCreateDto projectCreateDto) {
        log.info("Create project request: {}", ownerId);
        return projectFacade.create(projectCreateDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id,
                                    @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        log.info("Update project id={}", id);
        return projectFacade.update(projectUpdateDto, id);
    }

    @GetMapping("/filter")
    public List<ProjectDto> getProjectsByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            @RequestHeader("x-user-id") Long userId) {
        log.info("Filter projects request: name='{}', status='{}'", name, status);
        return projectFacade.getByFilter(name, status, userId);
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long id,
                                     @RequestHeader("x-user-id") Long userId) {
        log.info("Get project id={} by user {}", id, userId);
        return projectFacade.getById(id, userId);
    }

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        log.info("Get all projects request");
        return projectFacade.getAll();
    }
}