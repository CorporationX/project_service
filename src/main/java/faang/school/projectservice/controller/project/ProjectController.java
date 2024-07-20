package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("{userId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @PathVariable Long userId, @Valid @RequestBody ProjectDto projectDto) {
        return projectService.create(userId, projectDto);
    }

    @PutMapping("/update")
    public ProjectDto update(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @PostMapping("/{userId}/accessible/filtered")
    public List<ProjectDto> findProjectsWithFilter(@Valid @PathVariable Long userId,
                                                   @Valid @NotNull @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findProjectsWithFilter(userId, projectFilterDto);
    }

    @GetMapping("/{userId}/accessible/projects")
    public List<ProjectDto> findAllProjects(@Valid @PathVariable Long userId) {
        return projectService.findAllProjects(userId);
    }

    @PostMapping("/{userId}/accessible/project")
    public  ProjectDto findById(@Valid @PathVariable Long userId,
                                @Valid @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findById(userId, projectFilterDto);
    }
}
