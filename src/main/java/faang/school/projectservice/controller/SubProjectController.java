package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subprojects")
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping
    public SubProjectDto createSubProject(@RequestBody @Valid SubProjectDto subProjectDto) {
        return subProjectService.createSubProject(subProjectDto);
    }

    @PutMapping
    public SubProjectDto updateSubProject(@RequestBody @Valid SubProjectDto subProjectDto){
        return subProjectService.updateSubProject(subProjectDto);
    }

    @PostMapping("/{id}/search-by-filter")
    public List<SubProjectDto> getSubProjects(@RequestBody ProjectFilterDto projectFilter, @PathVariable("id") long projectId) {
        return subProjectService.getSubProjects(projectFilter, projectId);
    }
}
