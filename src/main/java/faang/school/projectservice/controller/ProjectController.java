package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    private static final String PROJECT_ID_PATH = "/{projectId}";

    @GetMapping(PROJECT_ID_PATH)
    public ProjectDto getProject(@PathVariable long projectId) {
        return projectMapper.toDto(projectService.getProjectById(projectId));
    }

}
