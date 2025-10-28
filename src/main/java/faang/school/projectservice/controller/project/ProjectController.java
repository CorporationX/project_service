package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.facade.project.ProjectFacade;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for managing projects")
public class ProjectController implements ProjectApi {

    private final ProjectFacade projectFacade;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        Long ownerId = userContext.getUserId();
        log.info("Create project request from ownerId={}", ownerId);
        return projectFacade.create(projectCreateDto, ownerId);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@Parameter(description = "ID проекта для обновления", example = "1")
                                    @PathVariable("id") Long id,
                                    @Valid @RequestBody ProjectUpdateDto projectUpdateDto, Long ownerId) {
        log.info("Update project id={}", id);
        return projectFacade.update(projectUpdateDto, id, ownerId);
    }

    @GetMapping("/filter")
    public List<ProjectDto> getProjectsByFilter(@Parameter(description = "Фильтр по названию проекта")
                                                @RequestParam(required = false) String name,
                                                @Parameter(description = "Фильтр по статусу проекта")
                                                @RequestParam(required = false) ProjectStatus status) {
        Long ownerId = userContext.getUserId();
        log.info("Filter projects request: ownerId={}, name='{}', status='{}'", ownerId, name, status);
        return projectFacade.getByFilter(name, status, ownerId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@Parameter(description = "ID проекта", example = "1")
                                     @PathVariable("id") Long id) {
        Long userId = userContext.getUserId();
        log.info("Get project by id={}, userId={}", id, userId);
        return projectFacade.getById(id, userId);
    }

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        log.info("Get all projects request");
        return projectFacade.getAll();
    }
}