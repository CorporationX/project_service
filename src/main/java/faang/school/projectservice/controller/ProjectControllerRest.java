package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@RestController
@RequestMapping("/api/projects")
public class ProjectControllerRest {

    private final ProjectServiceImpl projectService;

//    знаю, что будем брать userId из сессии. пока не умею. Потом можно будет переделать, наверное
    @PostMapping()
    public ProjectDto create(@RequestParam long userId, @RequestBody ProjectDto projectDto) {
        ProjectDto validatedDto = validateCreation(userId, projectDto);
        return projectService.create(validatedDto);
    }

    @PutMapping()
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        validateUpdate(projectDto);
        return projectService.update(projectDto);
    }

    @GetMapping("/filtered")
//    аналогично, потом будет userId из сессии
    public List<ProjectDto> getFilteredProjects(@RequestParam long userId, @RequestBody ProjectFilterDto projectDto) {
        return projectService.getFilteredProjects(userId, projectDto);
    }

    @GetMapping("/all")
    //    аналогично, потом будет userId из сессии
    public List<ProjectDto> getAllProjects(@RequestParam long userId) {
        ProjectFilterDto emptyDto = ProjectFilterDto.builder().build();
        return projectService.getFilteredProjects(userId, emptyDto);
    }

    @GetMapping("/byId/{projectId}")
    //    аналогично, потом будет userId из сессии
    public ProjectDto getProjectById(@RequestParam long userId,@PathVariable long projectId) {
        return projectService.getProjectById(userId, projectId);
    }

    private ProjectDto validateCreation(long userId, ProjectDto projectDto) {
        if (projectDto.getName() == null
                || projectDto.getDescription() == null
                || projectDto.getName().isBlank()
                || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Every project should have a name and a description");
        }
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userId);
        }
        return projectDto;
    }

    private void validateUpdate(ProjectDto projectDto) {
        if (projectDto.getId() == null) {
            throw new DataValidationException("Project for updating should be found by ID. Fill in this field.");
        }
        if (projectDto.getOwnerId() == null) {
            throw new DataValidationException("Project for updating should have ownerID. Fill in this field.");
        }
    }
}
