package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subproject")
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createSubProject(@RequestBody @Valid CreateSubProjectDto createSubProjectDto) {
        return subProjectService.createSubProject(createSubProjectDto);
    }

    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto updateSubProject(@PathVariable @NotNull Long projectId,
                                       @RequestBody ProjectDto projectDto) {
        return subProjectService.updateProject(projectId, projectDto);
    }

    @PostMapping("/{projectId}")
    public List<ProjectDto> getSubProjects(@PathVariable @NotNull Long projectId,
                                           @RequestBody ProjectFilterDto projectFilterDto) {
        return subProjectService.getSubProjects(projectId, projectFilterDto);
    }
}
