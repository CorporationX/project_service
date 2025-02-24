package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Проекты")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/projects")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @Operation(description = "Создать проект")
    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @Operation(description = "Обновить проект по ID")
    @PutMapping()
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @Operation(description = "Получить все проекты")
    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public List<ProjectDto> getAllProjects(@PathVariable @Min(0) int pageNumber,
                                           @PathVariable @Min(1) int pageSize,
                                           ProjectFilterDto filters) {
        return projectService.getAllProjects(pageNumber, pageSize, filters);
    }

    @Operation(description = "Получить проект по ID")
    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable long id) {
        return projectService.getProjectById(id);
    }
}
