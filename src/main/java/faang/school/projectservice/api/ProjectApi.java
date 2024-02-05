package faang.school.projectservice.api;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@RequestMapping(path = "/projects")
@Validated
@Tag(name = "Project", description = "The project API")
public interface ProjectApi {

    @PostMapping()
    @Operation(
            summary = "Создаёт и возвращает созданный проект",
            description = "Возвращает созданный проект после сохранения в базу",
            tags = {"Project"}
    )
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    ProjectDto create(@RequestBody @Valid ProjectDto projectDto);

    @PutMapping
    @Operation(
            summary = "Обновляет и возвращает обновленный проект",
            description = "Возвращает обновленный проект после сохранения в базу",
            tags = {"Project"}
    )
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    ProjectDto update(@RequestBody @Valid ProjectDto projectDto);

    @GetMapping
    @Operation(
            summary = "Возвращает все проекты",
            description = "Возвращает все проекты из базы",
            tags = {"Project"}
    )
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    List<ProjectDto> getAll();

    @GetMapping("{projectId}")
    @Operation(
            summary = "Возвращает проект по id",
            description = "Возвращает проект по его id из базы",
            tags = {"Project"}
    )
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    ProjectDto getById(@PathVariable @Min(1) long projectId);

    @GetMapping("/filters")
    @Operation(
            summary = "Возвращает отфильтрованные проекты",
            description = "Возвращает проекты по примененным фильтрам",
            tags = {"Project"}
    )
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    List<ProjectDto> getByFilters(@ModelAttribute ProjectFilterDto filterDto);
}
