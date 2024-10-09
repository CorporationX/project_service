package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.model.dto.project.ProjectDto;
import faang.school.projectservice.model.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/subproject")
@Validated
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createSubProject(@RequestBody @Valid @NotNull SubProjectDto projectId) {
        return subProjectService.createSubProject(projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateSubProject(@PathVariable @Positive @NotNull Long projectId, @RequestBody @Valid @NotNull SubProjectDto subProjectDto) {
        return subProjectService.updateSubProject(projectId, subProjectDto);
    }

    @PostMapping("/{projectId}")
    public List<ProjectDto> getAllSubProjectsWithFilter(@PathVariable @Positive @NotNull Long projectId, @RequestBody @Valid @NotNull ProjectFilterDto filterDto) {
        return subProjectService.getAllSubProjectsWithFilter(projectId, filterDto);
    }
}
