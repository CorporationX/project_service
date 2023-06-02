package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/v1/projects")
@Slf4j
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/list")
    public List<ProjectDto> getProjectsByFilter(@RequestBody FilterDto filter) {
        return projectService.getProjectsByFilter(filter);
    }

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectDto project) {
        if(nonNull(project.getId())) {
            log.warn("Id field must be null");
            return null;
        }
        return projectService.create(project);
    }

    @PutMapping
    public ProjectDto updateProject(@RequestBody ProjectDto project) {
        if(isNull(project.getId())) {
            log.warn("Id field must not be null");
            return null;
        }
        return projectService.update(project);
    }
}
