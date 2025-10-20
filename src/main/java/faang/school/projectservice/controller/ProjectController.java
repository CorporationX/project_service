package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface ProjectController {
    @Operation(summary = "Создать новый проект", description = "Создает новый проект с указанными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан",
                    content = @Content(schema = @Schema(implementation = ProjectDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "409", description = "Проект с таким именем уже существует")
    })
    ProjectDto create(ProjectCreateDto projectDto);

    @Operation(summary = "Обновить проект", description = "Обновляет существующий проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные или невозможно обновить проект"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    ProjectDto update(ProjectUpdateDto projectDto, long projectId);

    @Operation(summary = "Получить проект по ID", description = "Возвращает информацию о проекте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    ProjectDto getById(long projectId);

    @Operation(summary = "Получить список проектов",
            description = "Возвращает список проектов с пагинацией и сортировкой")
    List<ProjectDto> getAll(
            @Parameter(description = "Номер страницы (начинается с 0)")
            Integer page,
            @Parameter(description = "Размер страницы")
            Integer size,
            @Parameter(description = "Поле для сортировки")
            String sortBy,
            @Parameter(description = "Направление сортировки (ASC/DESC)")
            String sortDirection
    );


    @Operation(summary = "Поиск проектов с фильтрами",
            description = "Возвращает отфильтрованный список проектов")
    List<ProjectDto> search(ProjectFilterDto filterDto);

    @Operation(summary = "Удалить проект", description = "Удаляет проект по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    void delete(
            @Parameter(description = "ID проекта", required = true) long projectId
    );
}
