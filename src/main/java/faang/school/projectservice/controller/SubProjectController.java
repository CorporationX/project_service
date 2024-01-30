package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller("/api/v1/subProject")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    @ResponseBody
    public ProjectDto create(@RequestBody CreateSubProjectDto createSubProjectDto) {
        if (createSubProjectDto.getName().isBlank()) {
            throw new DataValidationException("Incorrect data");
        }
        return projectService.createSubProject(createSubProjectDto);
    }

    @PutMapping("/update/{projectId}")
    @ResponseBody
    public ProjectDto updateProject(@PathVariable Long projectId,
                                    @RequestBody UpdateSubProjectDto updateSubProjectDto) {
        isValidId(projectId);
        return projectService.updateProject(projectId, updateSubProjectDto);
    }

    @PostMapping("/findSubProject/{projectId}")
    @ResponseBody
    public List<ProjectDto> getFilteredPublicSubProjects(@PathVariable Long projectId,
                                                         @RequestBody ProjectFilterDto projectFilterDto) {
        isValidId(projectId);
        return projectService.getFilteredPublicSubProjects(projectId, projectFilterDto);
    }

    private static void isValidId(Long projectId) {
        if (projectId <= 0) {
            throw new DataValidationException("Incorrect id: " + projectId);
        }
    }
}