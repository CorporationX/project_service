package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.ContentDisposition;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/v1/resource/{projectId}")
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
        byte[] fileBytes;
        String filename;
        try (InputStream inputStream = resourceService.downloadResource(resourceId)) {
            fileBytes = inputStream.readAllBytes();
            filename = resourceService.getResourceName(resourceId);
            log.info("Resource with ID {} successfully downloaded, filename: {}", resourceId, filename);
        } catch (IOException e) {
            log.error("Error downloading resource with ID {}", resourceId, e);
            throw new RuntimeException("Error downloading resource", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
