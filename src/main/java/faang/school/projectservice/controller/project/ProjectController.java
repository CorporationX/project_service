package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService service;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return service.getAll();
    }

    @GetMapping("{id}")
    public ProjectDto getProject(@PathVariable @NotNull Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ProjectDto createProject(ProjectDto projectDto) {
        return service.create(projectDto);
    }

    @PostMapping("search")
    public List<ProjectDto> search(ProjectFilterDto filter) {
        return service.search(filter);
    }

    @PutMapping
    public ProjectDto updateProject(ProjectDto projectDto) {
        return service.update(projectDto);
    }
}
