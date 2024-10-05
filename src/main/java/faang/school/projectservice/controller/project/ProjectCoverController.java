package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.service.project.ProjectCoverService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/projects/{projectId}/cover")
public class ProjectCoverController {

    private final ProjectCoverService projectCoverService;

    @PostMapping
    public ProjectCoverDto uploadProjectCover(@PathVariable @Positive Long projectId,
                                              @RequestParam MultipartFile imageFile) {
        return projectCoverService.uploadCover(projectId, imageFile);
    }

    @PutMapping
    public ProjectCoverDto changeProjectCover(@PathVariable @Positive Long projectId,
                                                @RequestParam MultipartFile imageFile) {
        return projectCoverService.changeCover(projectId, imageFile);
    }

    @GetMapping
    public FileData getProjectCover(@PathVariable @Positive Long projectId) {
        return projectCoverService.getCover(projectId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectImage(@PathVariable @Positive Long projectId) {
        projectCoverService.deleteCover(projectId);
    }
}
