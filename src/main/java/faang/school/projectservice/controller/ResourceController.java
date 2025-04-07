package faang.school.projectservice.controller;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam("projectId") Long projectId,
                                               @RequestParam("uploaderId") Long uploaderId) throws IOException {
        Resource resource = fileStorageService.uploadFile(file, projectId, uploaderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long resourceId,
                                           @RequestParam("currentMemberId") Long currentMemberId) {
        fileStorageService.deleteFile(resourceId, currentMemberId);
        return ResponseEntity.noContent().build();
    }
}

