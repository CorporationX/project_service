package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
@Slf4j
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public List<ProjectDto> getProjectsByFilter(@RequestBody ProjectFilterDto filter) {
        return projectService.getProjectsByFilter(filter);
    }
}
