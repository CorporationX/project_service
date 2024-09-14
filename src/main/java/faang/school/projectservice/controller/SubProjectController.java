package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.validation.CreateGroup;
import faang.school.projectservice.dto.validation.UpdateGroup;
import faang.school.projectservice.service.subproject.SubProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
public class SubProjectController {

    private final SubProjectService subProjectService;

    @Operation(summary = "New Sub Project Creation")
    @PostMapping
    public SubProjectDto createSubProject(@Validated(CreateGroup.class) @RequestBody SubProjectDto subProjectDto) {
        return subProjectService.create(subProjectDto);
    }

    @Operation(summary = "Update the sub project")
    @PutMapping
    public SubProjectDto updateSubProject(@Validated(UpdateGroup.class) @RequestBody SubProjectDto subProjectDto) {
        return subProjectService.update(subProjectDto);
    }

    @Operation(summary = "Getting sub projects by parentId")
    @PostMapping("/sub-projects-by-filter/{parentId}")
    public List<SubProjectDto> getSubProjects(@PathVariable Long parentId, @RequestBody SubProjectFilterDto subProjectFilterDto) {
        return subProjectService.findSubProjectsByParentId(parentId, subProjectFilterDto);
    }
}
