package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.Marker;
import faang.school.projectservice.dto.filter.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @GetMapping("/filter")
    public List<ProjectDto> getProjectsByNameAndStatus(@Valid @RequestBody ProjectFilterDto filterDto) {
        return projectService.getProjectByNameAndStatus(filterDto);
    }

    @GetMapping()
    public List<ProjectDto> getAllProject() {
        return projectService.getAllProjectDto();
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable("id") @NotNull Long id) {
        return projectService.getProject(id);
    }
}
