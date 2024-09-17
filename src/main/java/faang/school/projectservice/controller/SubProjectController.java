package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.request.CreationRequest;
import faang.school.projectservice.dto.subproject.request.UpdatingRequest;
import faang.school.projectservice.service.subproject.SubProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
@RequestMapping("/projects/subprojects")
@Validated
public class SubProjectController {

    private final SubProjectService subProjectService;

    @Operation(summary = "New Sub Project Creation")
    @PostMapping
    public SubProjectDto createSubProject(@Valid @RequestBody CreationRequest creationRequest) {
        return subProjectService.create(creationRequest);
    }

    @Operation(summary = "Update the sub project")
    @PutMapping("/{projectId}")
    public SubProjectDto updateSubProject(@PathVariable @Positive Long projectId, @Valid @RequestBody UpdatingRequest updatingRequest) {
        return subProjectService.update(projectId, updatingRequest);
    }

    @Operation(summary = "Getting sub projects by parentId")
    @GetMapping("/sub-projects-by-filter/{parentId}")
    public List<SubProjectDto> getSubProjects(@PathVariable @Positive Long parentId, @RequestBody SubProjectFilterDto subProjectFilterDto) {
        return subProjectService.findSubProjectsByParentId(parentId, subProjectFilterDto);
    }
}
