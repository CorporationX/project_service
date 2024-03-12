package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectMapper;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.ion.NullValueException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto save(@RequestBody ProjectDto projectDto){
        Optional.ofNullable( projectDto ).orElseThrow(() -> new NullValueException("Project is Null") );
        return projectService.save(projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto update(@PathVariable long projectId,
                             @RequestParam(name = "description", required = false) String description,
                             @RequestParam(name = "status", required = false) String status){

        if(description !=null && status != null) projectService.update(description, status);
        return projectService.update(projectDto);
    }

    public List<ProjectDto> getProjects(@PathVariable(name = "status",required = false) String status,
                                        @PathVariable(name = "name", required = false) String name){

    }


}
