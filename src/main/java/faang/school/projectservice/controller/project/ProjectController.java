package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "projects", description = "Контроллеры для управления проектами")
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService service;

    @Operation(summary = "Получить все проекты", tags = {"projects", "get"})
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return service.getAll();
    }

    @Operation(summary = "Получить проект по id", tags = {"projects", "get"})
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable @NotNull Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Создать новый проект", tags = {"projects", "post"})
    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return service.create(projectDto);
    }

    @Operation(summary = "Обновить существующий проект", tags = {"projects", "put"})
    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return service.update(projectDto);
    }

    @Operation(summary = "Получить проекты с применением фильтров", tags = {"projects", "post"})
    @PostMapping("search")
    public List<ProjectDto> search(@Valid @RequestBody ProjectFilterDto filter) {
        return service.search(filter);
    }
}
