package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectImageService;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/projects")
@RequiredArgsConstructor
@RestController
public class ProjectController {
    private final UserContext userContext;
    private final ProjectImageService projectImageService;
    private final ProjectService projectService;
    private final ProjectMapper mapper;

    @GetMapping("/{projectId}")
    public ProjectDto findProject(@PathVariable long projectId) {
        Project project = projectService.findById(projectId);

        return mapper.toDto(project);
    }

    @PutMapping("/{projectId}/add/cover")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCoverImage(@PathVariable long projectId, @RequestBody MultipartFile file) {
        long ownerId = userContext.getUserId();
        projectImageService.addCoverImage(ownerId, projectId, file);
    }

    @GetMapping("/{projectId}/cover-image")
    public ResponseEntity<byte[]> getCoverImage(@PathVariable long projectId) {
        ResourceDownloadDto dto = projectImageService.getCoverImage(projectId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(dto.getType());
        headers.setContentDisposition(dto.getContentDisposition());

        return new ResponseEntity<>(dto.getBytes(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/cover-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoverImage(@PathVariable long projectId) {
        long ownerId = userContext.getUserId();
        projectImageService.deleteCoverImage(ownerId, projectId);
    }
}
