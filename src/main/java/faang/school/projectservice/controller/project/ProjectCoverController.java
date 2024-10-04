package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.exception.S3Exception;
import faang.school.projectservice.service.project.ProjectCoverService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/projects/{projectId}/cover")
public class ProjectCoverController {

    private final ProjectCoverService projectCoverService;

    @PostMapping
    public ResponseEntity<?> uploadProjectCover(@PathVariable @Positive Long projectId,
                                                @RequestParam MultipartFile imageFile) {
        return execute(() -> ResponseEntity.ok(projectCoverService.uploadCover(projectId, imageFile)));
    }

    @PutMapping
    public ResponseEntity<?> changeProjectCover(@PathVariable @Positive Long projectId,
                                                @RequestParam MultipartFile imageFile) {
        return execute(() -> ResponseEntity.ok(projectCoverService.changeCover(projectId, imageFile)));
    }

    @GetMapping
    public ResponseEntity<?> getProjectCover(@PathVariable @Positive Long projectId) {
        return execute(() -> {
            FileData fileData = projectCoverService.getCover(projectId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(fileData.getContentType()));
            return new ResponseEntity<>(fileData.getData(), headers, HttpStatus.OK);
        });
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteProjectImage(@PathVariable @Positive Long projectId) {
        return execute(() -> {
            projectCoverService.deleteCover(projectId);
            return ResponseEntity.noContent().build();
        });
    }

    private ResponseEntity<?> execute(Supplier<ResponseEntity<?>> operation) {
        try {
            return operation.get();
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected error occurred");
        }
    }
}
