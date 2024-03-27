package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;

import java.util.List;
import java.util.Optional;

import faang.school.projectservice.exception.DataValidationException;
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
    public ProjectDto createProject(@RequestBody ProjectDto projectDto, @RequestParam(name = "ownerId") Long ownerId) {
        validateParameters(projectDto);
        return projectService.createProject( projectDto, ownerId );

    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable long projectId,
                                    @RequestBody ProjectDto projectDto,
                                    @RequestParam(name = "requestUserId") long requestUserId) {
        validateParameters(projectDto);
        return projectService.updateProject( projectId, projectDto, requestUserId );
    }

    @GetMapping
    public List<ProjectDto> getProjectsByFilters(@RequestBody ProjectFilterDto filters,
                                                 @RequestParam(name = "requestUserId") long requestUserId) {
        validateParameters(filters);
        return projectService.findAllProjectsByFilters(filters, requestUserId);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects(@RequestParam long requestUserId) {
        return projectService.getAllProjects( requestUserId );
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable long projectId, @RequestParam long requestUserId) {
        return projectService.getProjectById( projectId, requestUserId );
    }

    private void validateParameters(ProjectFilterDto filters) {
        if (ObjectUtils.isEmpty(filters) || (filters.getNamePattern()==null && filters.getStatusPattern() == null)) {
            throw new DataValidationException("Filters  must not be empty");
        }
    }
    private void validateParameters(ProjectDto projectDto) {
        if (projectDto == null  || projectDto.getName() == null || projectDto.getDescription() == null ) {
            throw new DataValidationException("Project name and description must not be empty");
        }
    }


}
