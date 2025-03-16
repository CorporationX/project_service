package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{creatorId}/project/create")
    public ProjectDto create(ProjectDto projectDto, long creatorId) {
        return projectService.created(projectDto, creatorId);
    }

    @PatchMapping("/project/{projectId}/update")
    public ProjectDto update(long projectId, ProjectDto projectDto) {
        return projectService.update(projectId, projectDto);
    }
}