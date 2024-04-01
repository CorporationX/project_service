package faang.school.projectservice.controller.cover;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cover")
@Tag(name = "Cover", description = "Endpoints for managing cover")
public class CoverController {
    private final ResourceService resourceService;
    @PutMapping("/{projectId}/add")
    public ResourceDto addCover(@PathVariable Long projectId, @RequestParam("file") MultipartFile file){
        return resourceService.uploadCover(projectId, file);
    }
}
