package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.facade.project.ProjectFacade;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
public class ProjectController implements ProjectApi {

    private final ProjectFacade projectFacade;
    private final UserContext userContext;

    public ProjectDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        Long ownerId = userContext.getUserId();
        log.info("Create project request from ownerId={}", ownerId);
        return projectFacade.create(projectCreateDto, ownerId);
    }

    public ProjectDto updateProject(Long id, @Valid @RequestBody ProjectUpdateDto projectUpdateDto, Long ownerId) {
        log.info("Update project id={}", id);
        return projectFacade.update(projectUpdateDto, id, ownerId);
    }

    public List<ProjectDto> getProjectsByFilter(String name, ProjectStatus status) {
        Long ownerId = userContext.getUserId();
        log.info("Filter projects request: ownerId={}, name='{}', status='{}'", ownerId, name, status);
        return projectFacade.getByFilter(name, status, ownerId);
    }

    public ProjectDto getProjectById(Long id) {
        Long userId = userContext.getUserId();
        log.info("Get project by id={}, userId={}", id, userId);
        return projectFacade.getById(id, userId);
    }

    public List<ProjectDto> getAllProjects() {
        log.info("Get all projects request");
        return projectFacade.getAll();
    }
}