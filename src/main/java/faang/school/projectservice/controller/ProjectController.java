package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Projects", description = "API для управления проектами")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Создать новый проект", description = "Создает новый проект с указанными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан",
                    content = @Content(schema = @Schema(implementation = ProjectDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "409", description = "Проект с таким именем уже существует")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectCreateDto projectDto) {
        return projectService.create(projectDto);
    }

    @Operation(summary = "Обновить проект", description = "Обновляет существующий проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные или невозможно обновить проект"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PutMapping("/{projectId}")
    public ProjectDto update(
            @Valid @RequestBody ProjectUpdateDto projectDto,
            @Parameter(description = "ID проекта", required = true)
            @PathVariable @Positive long projectId) {
        return projectService.update(projectDto, projectId);
    }

    @Operation(summary = "Получить проект по ID", description = "Возвращает информацию о проекте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @GetMapping("/{projectId}")
    public ProjectDto getById(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable @Positive long projectId) {
        return projectService.getById(projectId);
    }

    @Operation(summary = "Получить список проектов",
            description = "Возвращает список проектов с пагинацией и сортировкой")
    @GetMapping
    public List<ProjectDto> getAll(
            @Parameter(description = "Номер страницы (начинается с 0)")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Размер страницы")
            @RequestParam(required = false) Integer size,
            @Parameter(description = "Поле для сортировки")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Направление сортировки (ASC/DESC)")
            @RequestParam(required = false) String sortDirection
    ) {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        return projectService.getByFilter(filterDto);
    }

    @Operation(summary = "Поиск проектов с фильтрами",
            description = "Возвращает отфильтрованный список проектов")
    @PostMapping("/search")
    public List<ProjectDto> search(@Valid @RequestBody ProjectFilterDto filterDto) {
        return projectService.getByFilter(filterDto);
    }

    @Operation(summary = "Удалить проект", description = "Удаляет проект по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable @Positive long projectId) {
        projectService.delete(projectId);
    }
}
