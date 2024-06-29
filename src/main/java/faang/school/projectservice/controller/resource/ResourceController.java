package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
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

    @PostMapping(value = "/{teamMemberId}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto saveFile(@PathVariable @Positive Long teamMemberId,
                                @RequestBody MultipartFile file) {
        return resourceService.saveFile(teamMemberId, file);
    }

    @GetMapping("/{teamMemberId}/files/{resourceId}")
    public InputStreamResource getFile(@PathVariable @Positive Long teamMemberId,
                                       @PathVariable @Positive Long resourceId) {
        return resourceService.getFile(teamMemberId, resourceId);
    }

    @DeleteMapping("/{teamMemberId}/files/{resourceId}")
    public void deleteFile(@PathVariable @Positive Long teamMemberId,
                           @PathVariable @Positive Long resourceId) {
        resourceService.deleteFile(teamMemberId, resourceId);
    }
}
