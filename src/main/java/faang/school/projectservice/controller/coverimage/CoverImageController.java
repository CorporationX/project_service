package faang.school.projectservice.controller.coverimage;

import faang.school.projectservice.service.coverimage.CoverImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cover-image")
public class CoverImageController {
    private final CoverImageService coverImageService;

    @PostMapping("/{projectId}")
    public void create(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        coverImageService.create(projectId, file);
    }

    @DeleteMapping("/{projectId}")
    public void delete(@PathVariable Long projectId) {
        coverImageService.delete(projectId);
    }
}
