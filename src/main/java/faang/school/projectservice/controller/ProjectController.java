package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;

import java.util.List;
import java.util.Optional;

import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectDto projectDto, @RequestParam(name = "ownerId") long ownerId) {
        validateParameters(projectDto, ownerId);
        return projectService.createProject( projectDto, ownerId );

    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable long projectId,
                                    @RequestBody ProjectDto projectDto,
                                    @RequestParam(name = "requestUserId") long requestUserId) {
        validateParameters(projectId, projectDto, requestUserId);
        return projectService.updateProject( projectId, projectDto, requestUserId );
    }

    @GetMapping
    public List<ProjectDto> getProjectsByFilters(@RequestBody ProjectFilterDto filters,
                                                 @RequestParam(name = "requestUserId") long requestUserId) {
        validateParameters(filters, requestUserId);
        return projectService.findAllProjectsByFilters(filters, requestUserId);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects(@RequestParam long requestUserId) {
        validateParameters(requestUserId);
        return projectService.getAllProjects( requestUserId );
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable long projectId, @RequestParam long requestUserId) {
        validateParameters(projectId, requestUserId);
        return projectService.getProjectById( projectId, requestUserId );
    }

    private void validateParameters(Object... parameters) {
        for (Object parameter : parameters) {
            if (ObjectUtils.isEmpty(parameter)) {
                throw new IllegalArgumentException("Parameters must not be empty");
            }
        }
    }


}
