package faang.school.projectservice.contreoller.project;

import faang.school.projectservice.api.ProjectApi;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {
    private final ProjectService projectService;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @Override
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @Override
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @Override
    public ProjectDto getById(@PathVariable @Min(1) long projectId) {
        return projectService.getById(projectId);
    }

    @Override
    public List<ProjectDto> getByFilters(@ModelAttribute ProjectFilterDto filterDto) {
        return projectService.getAll(filterDto);
    }
}