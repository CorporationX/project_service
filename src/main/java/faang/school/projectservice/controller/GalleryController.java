package faang.school.projectservice.controller;

import faang.school.projectservice.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @PostMapping("/{projectId}")
    public String uploadImage(@PathVariable Long projectId,
                              @RequestParam("file") MultipartFile file) {
        return galleryService.uploadImage(projectId, file);
    }

    @DeleteMapping("/{projectId}/{fileKey}")
    public void deleteImage(@PathVariable Long projectId,
                            @PathVariable String fileKey) {
        galleryService.deleteImage(projectId, fileKey);
    }

    @GetMapping("/{projectId}")
    public List<String> listImages(@PathVariable Long projectId) {
        return galleryService.getKeyListByProjectId(projectId);
    }
}
