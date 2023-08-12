package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/")
    public SubProjectDto createSubProject(@Valid @RequestBody SubProjectDto subProjectDto) {
        return projectService.createSubProject(subProjectDto);
    }

    @PutMapping("/")
    public LocalDateTime updateSubProject(@RequestBody UpdateSubProjectDto updateSubProjectDto) {
        return projectService.updateSubProject(updateSubProjectDto);
    }

    @PostMapping("/{projectId}/children")
    public List<SubProjectDto> getProjectChildrenWithFilter(@Valid @RequestBody ProjectFilterDto filterDto, @PathVariable long projectId) {
        return projectService.getProjectChildrenWithFilter(filterDto, projectId);
    }
}