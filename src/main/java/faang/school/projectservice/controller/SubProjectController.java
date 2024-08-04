package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.dto.validate.New;
import faang.school.projectservice.dto.validate.Update;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subprojects")
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping
    public SubProjectDto createSubProject(@Validated(New.class) @RequestBody SubProjectDto subProjectDto) {
        return projectService.createSubProject(subProjectDto);
    }

    @PutMapping
    public void updateSubProject(@Validated(Update.class) @RequestBody SubProjectDto subProjectDto) {
        projectService.updateSubProject(subProjectDto);
    }

    @GetMapping("/{parentProjectId}")
    public List<SubProjectDto> getAllFilteredSubprojectsOfAProject(@RequestBody SubProjectFilterDto subProjectFilterDto,
                                                                   @PathVariable Long parentProjectId) {
        return projectService.getAllFilteredSubprojectsOfAProject(subProjectFilterDto, parentProjectId);
    }
}
