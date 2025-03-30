package faang.school.projectservice.controller.presentation;

import faang.school.projectservice.service.presentation.PresentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class PresentationController {

    private final PresentationService presentationService;

    @PostMapping("/{projectId}/presentation/generate")
    public ResponseEntity<String> generatePresentation(
            @PathVariable("projectId") Long projectId
    ) {
        try {
            log.info("Generating presentation for project id: {}", projectId);
            String fileKey = presentationService.generateAndUploadPresentation(projectId);
            log.info("Presentation generated successfully with file key: {}", fileKey);
            return ResponseEntity.ok("Presentation generated successfully. FileKey = " + fileKey);
        } catch (Exception e) {
            log.error("Error generating presentation for project id {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating presentation: " + e.getMessage());
        }
    }

    @GetMapping("/presentations/{fileKey:.+}")
    public ResponseEntity<ByteArrayResource> downloadPresentation(@PathVariable("fileKey") String fileKey) {
        log.info("Request for download presentation with file key: {}", fileKey);
        try {
            log.info("Received download request for file with key: {}", fileKey);
            byte[] bytes = presentationService.downloadPresentation(fileKey);
            ByteArrayResource resource = new ByteArrayResource(bytes);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileKey + "\"");

            log.info("Successfully fetched and prepared file for download: {} ({} bytes)", fileKey, bytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(bytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error occurred while downloading file: {}", fileKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
