package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Endpoint for managing resources")
@RequestMapping("/v1")
public class ResourceController {
    private final ResourceService resourceService;
    @Operation(summary = "uploading file to project ")
    @PostMapping("/{userId}/upload/files")
    public ResourceDto uploadFileToProject(@PathVariable("userId") Long userId, @RequestBody MultipartFile file){
        return resourceService.uploadFileToProject(userId, file);
    }
}
