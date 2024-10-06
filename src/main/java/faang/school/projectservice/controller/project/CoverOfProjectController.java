package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.CoverOfProjectService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/project//{projectId}")
@RequiredArgsConstructor
public class CoverOfProjectController {

    private final CoverOfProjectService service;
    private final UserContext userContext;

    @PostMapping()
    public ResourceDto addResource(@PathVariable Long projectId,
                                   @RequestBody MultipartFile file)
                                   throws IOException, ImageReadException {
        return service.addResource(projectId, file);
    }

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable Long resourceId) {
        service.deleteResource(resourceId, userContext.getUserId());
    }

    @PutMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable Long resourceId,
                                      @RequestBody MultipartFile file) throws IOException, ImageReadException {
        return service.updateResource(resourceId, userContext.getUserId(), file);
    }
}
