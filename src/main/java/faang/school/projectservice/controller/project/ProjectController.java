package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping(ApiPath.PROJECTS_PATH)
@Tag(name = "Контроллер проекта")
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping
    @Operation(summary = "Создание проекта",
            description = "Создает новый проект с владельцем-текущий пользователь и статусом-создан")
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) {
        projectDto.setOwnerId(userContext.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectDto));
    }

    @PostMapping(ApiPath.FILTER_FUNCTIONALITY)
    @Operation(summary = "Фильтр",
            description = "Фильтрация по полям: Имя проекта, владелец проекта, статус, автор. Возрващает список проектов")
    public List<ProjectDto> filterProjects(@RequestBody ProjectFilterDto filters) {
        return projectService.filterProjects(filters);
    }

    @PutMapping(ApiPath.GENERAL_ID_PLACEHOLDER)
    @Operation(summary = "Обновить проект",
            description = "Возвращает дто сохоаненного проекта")
    public ProjectDto updateProject(@PathVariable("id") long id, @RequestBody @Valid ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(id, projectUpdateDto);
    }

    @GetMapping(ApiPath.GENERAL_ID_PLACEHOLDER)
    @Operation(summary = "Получить проект",
            description = "Возвращает дто проекта найденного по Id")
    public ProjectDto getProject(@PathVariable("id") long id) {
        return projectService.retrieveProject(id);
    }

    @GetMapping
    @Operation(summary = "Получить список всех проектов",
            description = "Возвращает список всех проектов")
    public List<ProjectDto> getProjects() {
        return projectService.getAllProjects();
    }
}