package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;

    public ProjectDto createSubProject(Long parentId, @Valid CreateSubProjectDto subProjectDto) {
        return projectService.createSubProject(parentId, subProjectDto);
    }

    public ProjectDto updateSubProject(Long projectId, @Valid ProjectDto projectDto) {
        return projectService.updateSubProject(projectId ,projectDto);
    }

    public List<ProjectDto> getSubProjects(Long projectId, SubProjectFilterDto filterDto) {
        return projectService.getSubProjects(projectId, filterDto);
    }
}
