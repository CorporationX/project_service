package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(long ownerId, String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Invalid name " + name);
        }
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Invalid description " + description);
        }
        return projectService.create(ownerId, name, description);
    }

    public ProjectDto update(long id, ProjectStatus status){
        return projectService.update(id,status);
    }

    public ProjectDto update(long id, String description){
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Invalid description " + description);
        }
        return projectService.update(id,description);
    }
    public ProjectDto update(long id, ProjectStatus status, String description){
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Invalid description " + description);
        }
        return projectService.update(id,status,description);
    }

    public List<ProjectDto> getProjectsWithFilters(long userId, String name){
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Invalid name " + name);
        }
        return projectService.getProjectsWithFilters(userId,name);
    }

    public List<ProjectDto> getProjectsWithFilters(long userId, ProjectStatus status){

        return projectService.getProjectsWithFilters(userId,status);
    }

    public List<ProjectDto> getProjectsWithFilters(long userId, String name, ProjectStatus status){

        return projectService.getProjectsWithFilters(userId,name,status);
    }

    public List<ProjectDto> getAllProjects(){
        return projectService.getAllProjects();
    }

    public ProjectDto getProjectById(long id){
        return projectService.getProjectById(id);
    }

}
