package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.resource.ResourceDto;
import faang.school.projectservice.service.FileServiceUpload;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/avi/v1")
public class FileController {
    private final FileServiceUpload serviceUpload;
    private final UserContext userContext;

    @PostMapping("/{projectId}")
    public ResponseEntity<ResourceDto> addFile(@RequestParam("file") MultipartFile file,
                                               @PathVariable long projectId) {
        ResourceDto result = serviceUpload.createFile(file, projectId, userContext.getUserId());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteFile(@PathVariable long resourceId) {
        serviceUpload.deleteFile(resourceId, userContext.getUserId());
        return ResponseEntity.ok("Delete was successful");
    }
}