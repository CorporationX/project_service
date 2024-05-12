package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto createProject(ProjectDto projectDto){
        return projectService.createProject(projectDto);
    }

    public ProjectDto updateProject(ProjectDto projectDto, ProjectStatus projectStatus, String description){
        return projectService.updateProject(projectDto, projectStatus, description);
    }

    public List<ProjectDto> getAllProjects(){
        return projectService.getAllProjects();
    }

    public ProjectDto getProjectById(Long projectId){
        return projectService.findProjectById(projectId);
    }

    public List<ProjectDto> findAllByFilter(ProjectFilterDto projectFilterDto, Long userId){
        return projectService.findAllByFilter(projectFilterDto, userId);
    }
}

