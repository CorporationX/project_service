package faang.school.projectservice.controller.resourse;

import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cover")
@Tag(name = "Cover", description = "Endpoints for managing cover")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(summary = "Removing a project cover")
    @DeleteMapping("/{key}/delete")
    public void deleteCover(@PathVariable String key) {
        resourceService.deleteResource(key);
    }
}
