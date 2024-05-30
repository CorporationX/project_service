package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService service;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable @NotNull Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return service.create(projectDto);
    }
    
    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return service.update(projectDto);
    }

    @PostMapping("search")
    public List<ProjectDto> search(@Valid @RequestBody ProjectFilterDto filter) {
        return service.search(filter);
    }
}
