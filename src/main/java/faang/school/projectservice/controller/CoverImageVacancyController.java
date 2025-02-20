package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CoverImageVacancyReadDto.ResourceDto;
import faang.school.projectservice.service.CoverImageService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/v1/vacancies")
@RequiredArgsConstructor
@Validated
public class CoverImageVacancyController {

    private final CoverImageService coverImageService;

    @Value("${coverImageVacancy.maxSizeFile}")
    private int maxSizeFile;

    @PostMapping("/{currentUserId}/{vacancyId}/resources")
    public ResourceDto uploadCover(@Min(1) @PathVariable long currentUserId,
                                   @Min(1) @PathVariable long vacancyId,
                                   @RequestBody(required = true)  MultipartFile file) {
        validationFile(file);
        return coverImageService.uploadCover(currentUserId, vacancyId, file);
    }

    @DeleteMapping("/{currentUserId}/{resourceId}/resources")
    public void deleteCover(@Min(1) @PathVariable long currentUserId,
                            @Min(1) @PathVariable long resourceId) {
        coverImageService.deleteCover(currentUserId, resourceId);
    }

    @GetMapping("/{resourceId}/resources")
    public InputStream getCoverImage(@Min(1) @PathVariable long resourceId) {
        return coverImageService.getCoverImage(resourceId);
    }

    private void validationFile(MultipartFile file) {

        if (file.isEmpty() || file == null) {
            throw new IllegalArgumentException("файл пуст");
        }

        if (file.getSize() > maxSizeFile) {
            throw new IllegalArgumentException("Размер файла не может превышать 5 Мб");
        }
    }
}
