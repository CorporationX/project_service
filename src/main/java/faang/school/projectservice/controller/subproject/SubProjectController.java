package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
public class SubProjectController {
    private final SubProjectService subProjectService;

    @Operation(summary = "Create subproject in database")
    @PostMapping("/create")
    public void createProject(@RequestBody CreateSubProjectDto createSubProjectDto) {
        subProjectService.createSubProject(createSubProjectDto);
    }
    @Operation(summary = "Update subproject in database")
    @PostMapping("/update")
    public void updateSubProject(@RequestBody SubProjectDto subprojectDto) {
        subProjectService.updateSubProject(subprojectDto);
    }
    @Operation(summary = "Get a filtered list of subprojects")
    @GetMapping("/list-subproject/{projectDto}")
    public List<ProjectDto> getAllProjectFilter(@PathVariable SubProjectDto projectDto, @RequestBody ProjectFilterDto projectFilterDto) {
        return subProjectService.getAllProjectFilter(projectDto, projectFilterDto);
    }
}