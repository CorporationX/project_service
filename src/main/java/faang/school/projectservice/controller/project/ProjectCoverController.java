package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.service.project.ProjectCoverService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/cover")
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
    public ResponseEntity<byte[]> getProjectCover(@PathVariable @Positive Long projectId) {
        FileData fileData = projectCoverService.getCover(projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(fileData.getData(), headers, HttpStatus.OK);
    }

    @DeleteMapping
    public void deleteProjectImage(@PathVariable @Positive Long projectId) {
        projectCoverService.deleteCover(projectId);
    }
}
