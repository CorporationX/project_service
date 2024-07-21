package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validation.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    @GetMapping("/project/{projectId}")
    public ProjectDto getProject(@PathVariable Long projectId){
        if(projectId == null){
            throw new DataValidationException("invalid id");
        }
        return projectService.getProject(projectId);
    }
}
