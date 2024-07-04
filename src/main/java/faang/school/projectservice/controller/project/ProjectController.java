package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "projects", description = "Контроллеры для управления проектами")
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Получение всех проектов", tags = {"projects"})
    @ApiResponse(responseCode = "200", description = "Все проекты успешно получены")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAll();
    }

    @Operation(summary = "Получение проектов согласно id", tags = {"projects"})
    @ApiResponse(responseCode = "200", description = "Проект успешно получен")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable @NotNull Long id) {
        return projectService.getById(id);
    }

    @Operation(summary = "Создание нового проекта", tags = {"projects"})
    @ApiResponse(responseCode = "201", description = "Новый проект успешно создан")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @Operation(summary = "Обновить существующий проект", tags = {"projects"})
    @ApiResponse(responseCode = "200", description = "Проекст успешно обновлен")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @Operation(summary = "Поиск проектов с применением фильтров", tags = {"projects"})
    @ApiResponse(responseCode = "200", description = "Проекты с учетом фильтров успешно получены")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping("search")
    public List<ProjectDto> search(@Valid @RequestBody ProjectFilterDto filter) {
        return projectService.search(filter);
    }

    @Operation(summary = "Проверка наличия проекта", tags = {"projects"})
    @ApiResponse(responseCode = "200", description = "Результат наличия проекта")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @GetMapping("/{projectId}/exists")
    public boolean existsById(@PathVariable long projectId) {
        return projectService.existsById(projectId);
    }

    @Operation(summary = "Поиск субпроектов с применением фильтров", tags = {"projects"})
    @ApiResponse(responseCode = "200", description = "Субпроекты с учетом фильтров успешно получены")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping("/{parentProjectId}/search")
    public List<ProjectDto> searchSubprojects(
        @Valid @RequestBody ProjectFilterDto filter,
        @PathVariable Long parentProjectId
    ) {
        return projectService.searchSubprojects(parentProjectId, filter);
    }
}
