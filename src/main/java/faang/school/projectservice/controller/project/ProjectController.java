package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.facade.project.ProjectFacade;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/projects")
@RestController
public class ProjectController {
    private final ProjectFacade projectFacade;
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectFacade.createProject(projectCreateDto);
    }

    @PatchMapping("/{id}")
    public ProjectDto updateProject(@PathVariable("id") long projectId, @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectFacade.updateProject(projectId, projectUpdateDto);
    }

    @PostMapping("/filtered")
    public List<ProjectDto> getProjectsByFilter(@Valid @RequestBody ProjectFilterDto projectFilterDto) {
        return projectFacade.getProjectsByFilter(projectFilterDto);
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable("id") long projectId) {
        return projectFacade.getProjectById(projectId);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable("id") long projectId) {
        projectFacade.deleteProject(projectId);
    }

    @PutMapping(value = "/cover-image/{projectId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResourceDto addImageCover(@PathVariable long projectId, @RequestPart("file")MultipartFile file) {
        return projectFacade.addImageCover(projectId, file);
    }

    @GetMapping("/cover-image/{projectId}")
    public ResponseEntity<Resource> getAvatarUsers(@PathVariable Long projectId ) {
        return projectService.getProjectAvatar(projectId);
    }
}
