package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
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
    public void deleteResource(@PathVariable long id) {
        resourceService.deleteResource(id, userContext.getUserId());
    }
}