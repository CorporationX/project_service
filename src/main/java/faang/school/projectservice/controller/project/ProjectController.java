package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.facade.project.ProjectFacade;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/projects")
@RestController
public class ProjectController {
    private final ProjectFacade projectFacade;

    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectFacade.createProject(projectCreateDto);
    }

    @PatchMapping("/{id}")
    public ProjectDto updateProject(@PathVariable("id") long projectId, @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectFacade.updateProject(projectId, projectUpdateDto);
    }

    @PostMapping("/filtered")
    public List<ProjectDto> getProjectsByFilter(@Valid @RequestBody ProjectFilterDto projectFilterDto) {
        return projectFacade.getProjectsByFilter(projectFilterDto);
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable("id") long projectId) {
        return projectFacade.getProjectById(projectId);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable("id") long projectId) {
        projectFacade.deleteProject(projectId);
    }
}
