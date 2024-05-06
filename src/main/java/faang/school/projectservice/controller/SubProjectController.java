package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;

    public ProjectDto createSubProject(long parentId, @Valid CreateSubProjectDto subProjectDto) {
        return projectService.createSubProject(parentId, subProjectDto);
    }

    public ProjectDto updateSubProject(long projectId, @Valid ProjectDto projectDto) {
        return projectService.updateSubProject(projectId ,projectDto);
    }
}
