package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    @PostMapping("/create")
    public faang.school.projectservice.dto.project.ProjectDto create(@RequestBody faang.school.projectservice.dto.project.ProjectDto projectDto) {
        projectValidator.checkIsNull(projectDto);
        return projectService.create(projectDto);
    }

    @PutMapping("/update")
    public faang.school.projectservice.dto.project.ProjectDto update(@RequestBody faang.school.projectservice.dto.project.ProjectDto projectDto) {
        projectValidator.checkIsNull(projectDto);
        return projectService.update(projectDto);
    }

    @PostMapping("/filter")
    public List<Project> getProjectsByFilter(@RequestBody ProjectFilterDto projectFilterDto) {
        projectValidator.checkIsNull(projectFilterDto);
        return projectService.getProjectsByFilter(projectFilterDto);
    }

    @GetMapping()
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable("id") Long projectId) {
        projectValidator.checkIsNull(projectId);
        return projectService.getProjectById(projectId);
    }

    @PostMapping("{projectId}/picture")
    public ProjectDto uploadProjectPicture(@PathVariable Long projectId, @RequestPart MultipartFile multipartFile) {
        projectValidator.checkProjectInDB(projectId);
        projectValidator.checkMaxSize(multipartFile);

        return projectService.uploadProjectPicture(projectId, multipartFile);
    }

    @GetMapping("{projectId}/picture")
    public ResponseEntity<byte[]> getProjectPicture(@PathVariable Long projectId) {
        projectValidator.checkProjectInDB(projectId);

        return projectService.getProjectPicture(projectId);
    }

    @DeleteMapping("{projectId}/picture")
    public ProjectDto deleteProfilePicture(@PathVariable Long projectId) {
        projectValidator.checkProjectInDB(projectId);

        return projectService.deleteProfilePicture(projectId);
    }
}