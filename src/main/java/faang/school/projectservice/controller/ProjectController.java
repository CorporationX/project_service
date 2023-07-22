package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/user/{userId}/create")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long userId) {
        return projectService.createProject(projectDto, userId);
    }

    @PostMapping("/user/{userId}/update")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long userId) {
        return projectService.updateProject(projectDto, userId);
    }
}
