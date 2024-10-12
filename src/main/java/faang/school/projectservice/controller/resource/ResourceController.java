package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/project/{projectId}/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        log.info("Request to add resource to project with ID: {}", projectId);
        ResourceDto resourceDto = resourceService.addResource(projectId, file);
        log.info("Resource successfully added to project with ID: {}", projectId);
        return resourceDto;
    }

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable Long resourceId) {
        log.info("Request to delete resource with ID: {}", resourceId);
        resourceService.deleteResource(resourceId);
        log.info("Resource with ID {} successfully deleted", resourceId);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {
        log.info("Request to download resource with ID: {}", resourceId);

        ResourceDownloadDto resource = resourceService.downloadResource(resourceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(resource.getFilename()).build());

        return new ResponseEntity<>(resource.getFileBytes(), headers, HttpStatus.OK);
    }
}
