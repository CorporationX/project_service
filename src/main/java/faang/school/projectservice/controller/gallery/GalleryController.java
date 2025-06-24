package faang.school.projectservice.controller.gallery;

import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.service.gallery.GalleryServiceFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gallery")
public class GalleryController {
    private final GalleryServiceFacade galleryServiceFacade;

    @GetMapping("/{projectId}")
    public ResponseEntity<Resource> getGalleryItem(
            @PathVariable long projectId,
            @RequestParam long resourceId
    ) {
        S3FileDto imageResourceDto = galleryServiceFacade.getGalleryItem(projectId, resourceId);

        return ResponseEntity.ok()
                .contentLength(imageResourceDto.getContentLength())
                .contentType(MediaType.parseMediaType(imageResourceDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + imageResourceDto.getFileName())
                .body(imageResourceDto.getResource());
    }

    @GetMapping("/{projectId}/items")
    public ResponseEntity<Page<ResponseResourceDto>> getGalleryPage(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(galleryServiceFacade.getGallery(projectId, page, size));
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<String> addToGallery(
            @PathVariable long projectId,
            @RequestParam MultipartFile[] files
    ) {
        galleryServiceFacade.addToGallery(projectId, files);
        return ResponseEntity.ok("Files uploaded successfully");
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> removeFromGallery(
            @PathVariable long projectId,
            @RequestParam long resourceId
    ) {
        galleryServiceFacade.deleteFile(projectId, resourceId);
        return ResponseEntity.ok("File %d deleted".formatted(resourceId));
    }
}