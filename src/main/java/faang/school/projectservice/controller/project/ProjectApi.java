package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

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
    ProjectDto createProject(ProjectCreateDto projectCreateDto);

    @Operation(
            summary = "Обновить существующий проект",
            description = "Обновляет описание или статус проекта по ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    ProjectDto updateProject(Long id, ProjectUpdateDto projectUpdateDto, Long ownerId);

    @Operation(
            summary = "Получить проекты по фильтру",
            description = "Возвращает список проектов, отфильтрованных по имени и/или статусу. Доступны проекты текущего пользователя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен")
    })
    List<ProjectDto> getProjectsByFilter(String name, ProjectStatus status);

    @Operation(
            summary = "Получить проект по ID",
            description = "Возвращает проект, если текущий пользователь имеет к нему доступ."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    ProjectDto getProjectById(Long id);

    @Operation(
            summary = "Получить все проекты",
            description = "Возвращает список всех существующих проектов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен")
    })
    List<ProjectDto> getAllProjects();
}
