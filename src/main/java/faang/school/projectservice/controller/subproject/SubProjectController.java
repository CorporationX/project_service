package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.ProjectFilterDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/subproject")
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createSubProject(@RequestBody @Valid SubProjectDto projectId) {
        return subProjectService.createSubProject(projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateSubProject(@PathVariable @NotNull Long projectId, @RequestBody @Valid SubProjectDto subProjectDto) {
        return subProjectService.updateSubProject(projectId, subProjectDto);
    }

    @PostMapping("/{projectId}")
    public List<ProjectDto> getAllSubProjectsWithFilter(@PathVariable @NotNull Long projectId, @RequestBody @Valid ProjectFilterDto filtrDto) {
        return subProjectService.getAllSubProjectsWithFiltr(projectId, filtrDto);
    }
}
