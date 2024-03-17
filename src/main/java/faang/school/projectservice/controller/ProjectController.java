package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;

import java.util.List;
import java.util.Optional;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
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
    public ProjectDto save(@RequestBody ProjectDto projectDto, @RequestParam(name = "ownerId") long ownerId) {
        Optional.ofNullable( projectDto ).orElseThrow( () -> new IllegalArgumentException( "Project is Null" ) );
        Optional.ofNullable( projectDto ).orElseThrow( () -> new IllegalArgumentException( "Provide owner ID " ) );
        Optional.ofNullable( projectDto.getName() ).orElseThrow( () -> new IllegalArgumentException( "Project name is Null" ) );
        Optional.ofNullable( projectDto.getDescription() ).orElseThrow( () -> new IllegalArgumentException( "Project description is null " ) );
        return projectService.createProject( projectDto, ownerId );

    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable long projectId,
                                    @RequestParam(name = "description", required = false) String description,
                                    @RequestParam(name = "status", required = false) String status,
                                    @RequestParam(name = "requestUserId") long requestUserId) {

        Optional.ofNullable( requestUserId ).orElseThrow( () -> new IllegalArgumentException( "User ID must not be null" ) );

        if (status == null && description == null) {
            throw new IllegalArgumentException( "Provide description and/or status to change" );
        }
        ProjectStatus projectStatus = ProjectStatus.valueOf( status );
        return projectService.updateProject( projectId, description, projectStatus,requestUserId );
    }

    @GetMapping
    public List<ProjectDto> getProjectsByNameOrStatus(@RequestParam(name = "name", required = false) String name,
                                                      @RequestParam(name = "status", required = false) String status,
                                                      @RequestParam(name = "requestUserId") long requestUserId) {
        Optional.ofNullable( requestUserId ).orElseThrow( () -> new IllegalArgumentException( "User ID must not be null" ) );
        if (status == null && name == null) {
            throw new IllegalArgumentException( "Provide name or status to search" );
        }
        if (name != null) {
            return projectService.findProjectsByName( name, requestUserId );
        } else {
            return projectService.findProjectsByStatus( ProjectStatus.valueOf( status.toUpperCase() ), requestUserId );
        }
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects(@RequestParam long requestUserId) {
        Optional.ofNullable( requestUserId ).orElseThrow( () -> new IllegalArgumentException( "User ID must not be null" ) );
        return projectService.getAllProjects( requestUserId );
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable long projectId, @RequestParam long requestUserId) {
        Optional.ofNullable( projectId ).orElseThrow( () -> new IllegalArgumentException( "Project ID must not be null" ) );
        Optional.ofNullable( requestUserId ).orElseThrow( () -> new IllegalArgumentException( "User ID must not be null" ) );
        return projectService.getProjectById( projectId, requestUserId );
    }


}
