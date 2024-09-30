package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.file.ResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId,
                                   @RequestBody MultipartFile file)
                                   throws IOException, ImageReadException {
        return resourceService.addResource(projectId, file);
    }

    @DeleteMapping("/{resourceId}/delete")
    public void deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId, userContext.getUserId());
    }

    @PostMapping("/{resourceId}/update")
    public ResourceDto updateResource(@PathVariable Long resourceId,
                                      @RequestBody MultipartFile file) throws IOException, ImageReadException {
        return resourceService.updateResource(resourceId, userContext.getUserId(), file);
    }
}
