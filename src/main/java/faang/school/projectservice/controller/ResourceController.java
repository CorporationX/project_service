package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }
}
