package faang.school.projectservice.controller;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/{id}/image")
public class CoverImageController {
    private static final BigInteger MAX_COVER_IMAGE_SIZE = BigInteger.valueOf(5 * 1024 * 1024);

    private final ProjectService projectService;

    @PostMapping
    public void addCoverImage(@PathVariable Long id, @RequestBody MultipartFile coverImage) {
        if (BigInteger.valueOf(coverImage.getSize()).compareTo(MAX_COVER_IMAGE_SIZE) > 0) {
            throw new DataValidationException("The size of cover image is greater than " + MAX_COVER_IMAGE_SIZE);
        }
        projectService.addCoverImage(id, coverImage);
    }

    @PutMapping
    public void updateCoverImage(@PathVariable Long id, @RequestBody MultipartFile coverImage) {
        if (BigInteger.valueOf(coverImage.getSize()).compareTo(MAX_COVER_IMAGE_SIZE) > 0) {
            throw new DataValidationException("The size of cover image is greater than " + MAX_COVER_IMAGE_SIZE.longValue());
        }
        projectService.updateCoverImage(id, coverImage);
    }

    @GetMapping
    public InputStream getCoverImage(@PathVariable Long id) {
        return projectService.getCoverImage(id);
    }

    @DeleteMapping
    public void deleteCoverImage(@PathVariable Long id) {
        projectService.deleteCoverImage(id);
    }
}
