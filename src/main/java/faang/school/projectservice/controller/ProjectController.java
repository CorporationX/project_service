package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PutMapping("/{projectId}/cover/add")
    public ResourceDto addCover(@PathVariable long projectId, @RequestBody MultipartFile file) {
        return resourceService.addCoverToProject(projectId, userContext.getUserId(), file);
    }

    @GetMapping("/cover/{resourceId}")
    public ResponseEntity<byte[]> getCover(@PathVariable long resourceId) {
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadCover(resourceId).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.OK);
    }
}
