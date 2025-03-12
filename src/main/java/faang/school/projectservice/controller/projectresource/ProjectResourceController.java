package faang.school.projectservice.controller.projectresource;

import faang.school.projectservice.dto.projectresource.ProjectResourceDto;
import faang.school.projectservice.mapper.ProjectResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.projectresource.ProjectResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProjectResourceController {
    private final ProjectResourceService projectResourceService;
    private final ProjectResourceMapper projectResourceMapper;

    @PostMapping("/{projectResourceId}")
    public ProjectResourceDto addResource(@PathVariable @Positive long projectResourceId,
                                          @RequestParam @Positive Long userId,
                                          @RequestBody MultipartFile file) {
        Resource resource = projectResourceService.addFile(projectResourceId, userId, file);
        return projectResourceMapper.toDto(resource);
    }

    @PutMapping("/{projectResourceId}")
    public ProjectResourceDto updateResource(@PathVariable @Positive long projectResourceId,
                                             @RequestParam @Positive Long userId,
                                             @RequestBody MultipartFile file) {
        Resource resource = projectResourceService.updateFile(projectResourceId, userId, file);
        return projectResourceMapper.toDto(resource);
    }

    @DeleteMapping("/{projectResourceId}")
    public ProjectResourceDto deleteResource(@PathVariable @Positive long projectResourceId,
                                             @RequestParam @Positive Long userId) {
        Resource resource = projectResourceService.removeFile(projectResourceId, userId);
        return projectResourceMapper.toDto(resource);
    }
    @GetMapping("/gallery/{projectResourceId}")
    public List<String> getGalleryImages(@PathVariable @Positive long projectResourceId) {
        return  projectResourceService.getAllProjectImages(projectResourceId);
    }
}
