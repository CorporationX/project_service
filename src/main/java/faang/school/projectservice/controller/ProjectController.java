package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public void createProject(ProjectDto projectDto) {
        projectService.createProject(projectDto);
    }
}
