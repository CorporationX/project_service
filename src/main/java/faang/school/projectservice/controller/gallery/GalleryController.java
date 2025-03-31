package faang.school.projectservice.controller.gallery;

import faang.school.projectservice.dto.gallery.GalleryDeleteFilesRequestDto;
import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import faang.school.projectservice.service.gallery.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${spring.servlet.mvc.path}/gallery")
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;

    @PostMapping(value = "/project/{projectId}/upload", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<GalleryResponseDto> uploadFiles(
            @PathVariable("projectId") long projectId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return new ResponseEntity<>(galleryService.uploadFiles(projectId, files), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFiles(@RequestBody GalleryDeleteFilesRequestDto request) {
        galleryService.deleteFiles(request.getKeys());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/project/{projectId}/download")
    public ResponseEntity<List<String>> downloadFiles(@PathVariable("projectId") long projectId
    ) {
        List<String> base64Images = galleryService.downloadImagesAsBase64(projectId);
        return ResponseEntity.ok(base64Images);
    }
}