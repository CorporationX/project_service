package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping(value = "/{userId}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto saveFile(@PathVariable @Positive Long userId,
                                @NotNull @RequestBody MultipartFile file) {
        return resourceService.saveFile(userId, file);
    }

    @GetMapping("/{userId}/files/{resourceId}")
    public InputStreamResource getFile(@PathVariable @Positive Long userId,
                                       @PathVariable @Positive Long resourceId) {
        return resourceService.getFile(userId, resourceId);
    }

    @DeleteMapping("/{userId}/files/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable @Positive Long userId,
                           @PathVariable @Positive Long resourceId) {
        resourceService.deleteFileFromProject(userId, resourceId);
    }
}
