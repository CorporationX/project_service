package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectController {

    private ProjectServiceImpl projectService;

    public ProjectDto create (ProjectDto projectDto){
// todo: валидация
        return projectService.create(projectDto);
    }

    public ProjectDto update (long userId, ProjectDto projectDto){
        // todo: валидация
        return projectService.update(userId, projectDto);
    }

    public List<ProjectDto> getFilteredProjects(long userId, ProjectDto dto){
        // todo: валидация
        return  projectService.getFilteredProjects(userId, dto);
    }
}
