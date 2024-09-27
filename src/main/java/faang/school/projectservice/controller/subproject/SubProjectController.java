package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.subproject.SubProjectService;
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
    public ProjectDto createSubProject(@RequestBody @Validated SubProjectDto projectId) {
        return subProjectService.createSubProject(projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateSubProject(@PathVariable @Positive Long projectId, @RequestBody @Validated SubProjectDto subProjectDto) {
        return subProjectService.updateSubProject(projectId, subProjectDto);
    }

    @PostMapping("/{projectId}")
    public List<ProjectDto> getAllSubProjectsWithFilter(@PathVariable @Positive Long projectId, @RequestBody @Validated ProjectFilterDto filterDto) {
        return subProjectService.getAllSubProjectsWithFilter(projectId, filterDto);
    }
}
