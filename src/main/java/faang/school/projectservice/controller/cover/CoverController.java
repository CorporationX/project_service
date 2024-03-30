package faang.school.projectservice.controller.cover;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.cover.CoverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
@Tag(name = "Resource", description = "Endpoints for managing resource")
public class CoverController {
    private final CoverService coverService;

    public ProjectDto addCover(@PathVariable Long projectId, @RequestBody MultipartFile file){
        return coverService.addCover(projectId, file);
    }
}
