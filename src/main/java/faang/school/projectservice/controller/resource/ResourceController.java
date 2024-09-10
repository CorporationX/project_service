package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{projectId}")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }

    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {
        byte[] fileBytes = null;
        Pair<MediaType, InputStream> pair = resourceService.downloadResource(resourceId);
        try {
            fileBytes = pair.getSecond().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(pair.getFirst());
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @PutMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable Long resourceId, @RequestParam("file") MultipartFile file) {
        return resourceService.updateResource(resourceId, file);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.ok("Resource deleted successfully.");
    }
}
