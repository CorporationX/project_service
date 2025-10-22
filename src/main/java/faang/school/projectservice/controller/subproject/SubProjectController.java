package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.subproject.SubProjectServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/subprojects")
public class SubProjectController {

    private final SubProjectServiceImpl subProjectService;

    @PostMapping
    public SubProjectDto create(@RequestBody @Valid CreateSubProjectDto createSubProjectDto) {
        return subProjectService.createSubProject(createSubProjectDto);
    }

    @PatchMapping("/{subProjectId}")
    public SubProjectDto update(
            @PathVariable @Positive Long subProjectId,
            @RequestBody @Valid UpdateSubProjectDto updateSubProjectDto) {
        return subProjectService.updateSubProject(updateSubProjectDto, subProjectId);
    }

    @GetMapping("/{projectId}")
    public List<SubProjectDto> getSubProjects(
            @PathVariable @Positive Long projectId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status
    ) {
        return subProjectService.getSubProjects(projectId, name, status);
    }
}
