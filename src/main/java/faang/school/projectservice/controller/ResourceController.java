package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PutMapping("/{projectId}/add")
    public ResourceDto addCoverToProject(@PathVariable Long projectId, @RequestBody MultipartFile multipartFile) {
        return resourceService.addCoverToProject(projectId, multipartFile);
    }
}