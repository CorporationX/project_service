package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subProject")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        validateName(createSubProjectDto);
        return projectService.createSubProject(createSubProjectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable Long projectId,
                                    @RequestBody UpdateSubProjectDto updateSubProjectDto) {
        validateId(projectId);
        return projectService.updateProject(projectId, updateSubProjectDto);
    }

    @PostMapping("/search/{projectId}")
    public List<ProjectDto> getFilteredSubProjects(@PathVariable Long projectId,
                                                   @RequestBody ProjectFilterDto projectFilterDto) {
        validateId(projectId);
        return projectService.getFilteredSubProjects(projectId, projectFilterDto);
    }

    private void validateId(Long projectId) {
        if (projectId <= 0) {
            throw new DataValidationException("Incorrect id: " + projectId);
        }
    }

    private void validateName(CreateSubProjectDto createSubProjectDto) {
        if (createSubProjectDto.getName().isBlank()) {
            throw new DataValidationException("Incorrect data");
        }
    }
}