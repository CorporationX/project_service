package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "Projects", description = "Endpoints for managing projects")
@RequestMapping("/api/v1/projects")
public interface ProjectApi {

    @Operation(
            summary = "Создать новый проект",
            description = "Создаёт новый проект для текущего пользователя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Проект успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "409", description = "Проект с таким именем уже существует"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProjectDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto);

    @Operation(
            summary = "Обновить существующий проект",
            description = "Обновляет описание или статус проекта по ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PatchMapping("/{id}")
    ProjectDto updateProject(
            @Parameter(description = "ID проекта для обновления", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto,
            @PathVariable Long ownerId);

    @Operation(
            summary = "Получить проекты по фильтру",
            description = "Возвращает список проектов, отфильтрованных по имени и/или статусу. Доступны проекты текущего пользователя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен")
    })
    @GetMapping("/filter")
    List<ProjectDto> getProjectsByFilter(
            @Parameter(description = "Фильтр по названию проекта", example = "MyProject")
            @RequestParam(required = false) String name,
            @Parameter(description = "Фильтр по статусу проекта", example = "CREATED")
            @RequestParam(required = false) ProjectStatus status);

    @Operation(
            summary = "Получить проект по ID",
            description = "Возвращает проект, если текущий пользователь имеет к нему доступ."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @GetMapping("/{id}")
    ProjectDto getProjectById(
            @Parameter(description = "ID проекта", example = "1")
            @PathVariable Long id);

    @Operation(
            summary = "Получить все проекты",
            description = "Возвращает список всех существующих проектов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен")
    })
    @GetMapping
    List<ProjectDto> getAllProjects();
}
