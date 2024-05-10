package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(@NonNull ProjectDto project){
        return projectService.create(project);
    }
}
