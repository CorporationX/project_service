package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PostMapping("/{projectId}/resources")
    public ResponseEntity<ResponseResourceDto> addResource(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        Resource resource = resourceService.addResource(projectId, file);
        ResponseResourceDto response = resourceMapper.toDto(resource);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}/resources/{resourceId}")
    public ResponseEntity<Boolean> deleteResource(@PathVariable Long projectId, @PathVariable Long resourceId) {
        Boolean resource = resourceService.deleteResource(projectId, resourceId);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{projectId}/resources/{resourceId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadResource(@PathVariable Long projectId, @PathVariable Long resourceId) {
        S3FileDto fileDto = resourceService.downloadFile(projectId, resourceId);
        return ResponseEntity.ok()
                .contentLength(fileDto.getContentLength())
                .contentType(MediaType.parseMediaType(fileDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + fileDto.getFileName())
                .body(fileDto.getResource());
    }

    @PatchMapping("/{projectId}/resources/{resourceId}")
    public ResponseEntity<ResponseResourceDto> updateResource(@PathVariable Long projectId, @PathVariable Long resourceId,
                                                              @RequestParam("file") MultipartFile file) {
        Resource resource = resourceService.updateResource(projectId, resourceId, file);
        ResponseResourceDto response = resourceMapper.toDto(resource);
        return ResponseEntity.ok(response);
    }

}
