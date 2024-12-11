package faang.school.projectservice.controller.project;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectBucketController {
    private final ProjectService projectService;

    @PostMapping(value = "/{projectId}/add-cover-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addCoverImage(@PathVariable long projectId,
                                @RequestParam("coverImage") MultipartFile coverImage)
            throws IOException {
        MultipartFile compressedImage = projectService.validateImageResolution(coverImage);
        log.info("Received a request to add cover image for project {}", projectId);
        return projectService.uploadImage(projectId, compressedImage);
    }
}
