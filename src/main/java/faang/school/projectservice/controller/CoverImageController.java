package faang.school.projectservice.controller;

import faang.school.projectservice.dto.coverImageVacancy.ResourceDto;
import faang.school.projectservice.service.CoverImageService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/v1/vacancies")
@AllArgsConstructor
@Validated
public class CoverImageController {
    private final CoverImageService coverImageService;

    @PostMapping("/{currentUserId}/{vacancyId}/resources")
    public ResourceDto uploadCover(@Min(1) @PathVariable Long currentUserId,
                                   @Min(1) @PathVariable Long vacancyId, MultipartFile file) {
        validationFile(file);
        return coverImageService.uploadCover(currentUserId, vacancyId, file);
    }

    @DeleteMapping("/{currentUserId}/{resourceId}/resources")
    public void deleteCover(@Min(1) @PathVariable Long currentUserId,
                            @Min(1) @PathVariable Long resourceId) {
        coverImageService.deleteCover(currentUserId, resourceId);
    }

    @GetMapping("/{resourceId}/resources")
    public InputStream getCoverImage(@Min(1) @PathVariable Long resourceId) {
        return coverImageService.getCoverImage(resourceId);
    }

    private void validationFile(MultipartFile file) {
        final int maxSizeFile = 5 * 1024 * 1024;
        if (file.isEmpty()) {
            log.error("файл пуст");
            throw new IllegalArgumentException("файл пуст");
        }

        if (file.getSize() > maxSizeFile) {
            log.error("Размер файла превышает 5 Мб");
            throw new IllegalArgumentException("Размер файла не может превышать 5 Мб");
        }
    }
}
