package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")

@ConditionalOnProperty(value = "services.s3", havingValue = "true")
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping("/upload")
    public ResourceDto uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("resourceDto") @Valid ResourceDto resourceDto) {
        return resourceService.uploadFile(resourceDto, file, userContext.getUserId());
    }

    @PutMapping("/upload/{id}")
    public ResourceDto updateFile(
            @PathVariable long id,
            @RequestPart("file") MultipartFile file,
            @RequestPart("resourceDto") @Valid ResourceDto resourceDto) {
        return resourceService.updateFile(id, resourceDto, file, userContext.getUserId());
    }

    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable long id) {
        resourceService.deleteResource(id, userContext.getUserId());
    }
}
