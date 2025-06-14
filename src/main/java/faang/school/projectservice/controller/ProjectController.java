package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.RequestFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping("/create")
    public ProjectDto create(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.create(projectDto, userContext.getUserId());
    }

    @PatchMapping("{projectId}/update")
    public ProjectDto update(@RequestBody @Valid ProjectDto projectDto, @PathVariable @Min(1) long projectId) {
        return projectService.update(projectId, projectDto);
    }

    @GetMapping("/projects/get-filter")
    public Page<ProjectDto> getFilteredProjects(
            @RequestBody RequestFilterDto filter,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return projectService.findProjectsByFiltersAndAccess(filter, pageable);
    }

    @GetMapping("/projects/{projectId}/get-id")
    public ProjectDto findByIdProject(@PathVariable @Min(1) long projectId) {
        return projectService.findByIdProject(projectId);
    }

    @GetMapping("/get-all")
    public Page<ProjectDto> getAllProjects(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC) Pageable pageable){
        return projectService.getAllProjects(pageable);
    }
}