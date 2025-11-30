package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ResourceService resourceService;
    private final ProjectService projectService;

    @PostMapping("/{projectId}/resources")
    public ResourceDto addResource(@PathVariable long projectId, @RequestBody MultipartFile file) {
            ResourceDto resourceDto = resourceService.addCover(projectId, file);
            projectService.addCover(projectId, resourceDto);
            return resourceDto;
    }
}
