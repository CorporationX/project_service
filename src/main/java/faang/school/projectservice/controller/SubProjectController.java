package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/subProject/create")
    public SubProjectDto createSubProject(@RequestBody SubProjectDto projectDto) {
        return projectService.createSubProject(projectDto);
    }

    @PostMapping("/subProjects/create")
    public List<SubProjectDto> createSubProjects(@RequestBody List<SubProjectDto> projectsDtos) {
        return projectService.createSubProjects(projectsDtos);
    }

    @PutMapping("/subProject/update")
    public Timestamp updateSubProject(@RequestBody SubProjectDto projectDto) {
        return projectService.updateSubProject(projectDto);
    }

    @PostMapping("/subProject/{projectId}/children")
    public List<SubProjectDto> getProjectChildrenWithFilter(@RequestBody ProjectFilterDto filterDto, @PathVariable long projectId) {
        return projectService.getProjectChildrenWithFilter(filterDto, projectId);
    }
}