package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
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

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/project/{projectId}/upload")
    public ResponseEntity<ResourceDto> uploadFile(@PathVariable("projectId") long projectId,
                                                  @RequestBody MultipartFile file) {
        return new ResponseEntity<>(resourceService.uploadFile(projectId, file), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("id") String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.add("Content-Disposition", String.format("attachment; filename=\"%s\"", id));
        InputStream inputStream = resourceService.downloadFile(id);
        return new ResponseEntity<>(new InputStreamResource(inputStream), httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") String id) {
        resourceService.deleteFile(id);
        return ResponseEntity.ok().build();
    }
}
