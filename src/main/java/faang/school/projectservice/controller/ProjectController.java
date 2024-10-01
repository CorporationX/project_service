package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.project.ProjectService;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;

    @PostMapping("{project-id}/files")
    public ResourceDto uploadFileToProject(@PathVariable("project-id") Long projectId,
                                           @RequestParam("user-id") Long userId,
                                           @RequestBody MultipartFile file) {
        Resource resource = projectService.uploadFileToProject(projectId, userId, file);
        return resourceMapper.toResourceDto(resource);
    }

    @DeleteMapping("{project-id}/files")
    public void deleteFileFromProject(@PathVariable("project-id") Long projectId,
                                    @RequestParam("user-id") Long userId,
                                    @RequestParam("resource-id") Long resourceId) {
        projectService.deleteFileFromProject(projectId, userId, resourceId);

    }


    @GetMapping("{project-id}/files")
    public ResponseEntity<InputStreamResource> getFileFromProject(@PathVariable("project-id") Long projectId,
                                                                  @RequestParam("user-id") Long userId,
                                                                  @RequestParam("resource-id") Long resourceId) throws IOException {
        Map<Resource, InputStream> resourceWithFileStream = projectService.getFileFromProject(projectId, userId, resourceId);
        Resource resource = resourceWithFileStream.keySet().iterator().next();
        InputStream fileStream = resourceWithFileStream.get(resource);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getName() + "\"")
                .body(new InputStreamResource(fileStream));
    }
}
