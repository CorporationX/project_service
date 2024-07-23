package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subproject")
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping
    public ProjectDto createSubProject(@Valid CreateSubProjectDto subProjectDto) {
        return subProjectService.createSubProject(subProjectDto);
    }
    @PutMapping
    public ProjectDto updateSubProject(UpdateSubProjectDto subProjectDto) {
        return subProjectService.updateProject(subProjectDto);
    }
    @GetMapping
    public List<ProjectDto> getSubProjects(SubProjectDtoFilter filter) {
        return subProjectService.getProjects(filter);
    }
}
