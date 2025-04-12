package faang.school.projectservice.controller.presentation;

import faang.school.projectservice.service.presentation.PresentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
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

    @PostMapping("/{projectId}/presentation")
    public ResponseEntity<String> generatePresentation(
            @PathVariable("projectId") Long projectId
    ) {
        log.info("Generating presentation for project id: {}", projectId);
        String fileKey = presentationService.generateAndUploadPresentation(projectId);
        log.info("Presentation generated successfully with file key: {}", fileKey);
        return ResponseEntity.ok("Presentation generated successfully. FileKey = " + fileKey);
    }

    @GetMapping("/presentations/{fileKey:.+}")
    public ResponseEntity<ByteArrayResource> downloadPresentation(@PathVariable("fileKey") String fileKey) {
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
    }
}
