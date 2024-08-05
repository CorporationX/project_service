package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.resource.ResourceDto;
import faang.school.projectservice.service.FileServiceUpload;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/avi/v1")
public class FileController {
    private final FileServiceUpload serviceUpload;
    private final UserContext userContext;

    @PostMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto addFile(@RequestParam("file") MultipartFile file,
                               @PathVariable long projectId) {
        return serviceUpload.createFile(file, projectId, userContext.getUserId());
    }

    @DeleteMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable long resourceId) {
        serviceUpload.deleteFile(resourceId, userContext.getUserId());
    }
}