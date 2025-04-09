package faang.school.projectservice.controller;

import org.springframework.core.io.Resource;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies/{vacancyId}")
@Tag(name = "Vacancy", description = "Vacancy API")
public class VacancyController {
    private final VacancyService vacancyService;

    @Operation(
        summary = "Upload vacancy cover",
        description = "Upload or update vacancy cover"
    )
    @PostMapping("/cover")
    public ResponseEntity<Void> uploadCover(
            @Parameter(description = "Vacancy ID", example = "1234", required = true)
            @PathVariable
            @NotNull Long vacancyId,
            @Parameter(description = "Vacancy cover file", required = true)
            @RequestParam("cover")
            @NotNull MultipartFile image) {
        vacancyService.uploadCover(vacancyId, image);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get vacancy cover",
        description = "Get vacancy cover"
    )
    @GetMapping("/cover")
    public ResponseEntity<Resource> getCover(
            @Parameter(description = "Vacancy ID", example = "1234", required = true)
            @PathVariable
            @NotNull Long vacancyId) {
        Resource resource = vacancyService.getCover(vacancyId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @Operation(
        summary = "Delete vacancy cover",
        description = "Delete vacancy cover"
    )
    @DeleteMapping("/cover")
    public ResponseEntity<Void> deleteCover(
            @Parameter(description = "Vacancy ID", example = "1234", required = true)
            @PathVariable
            @NotNull Long vacancyId) {
        vacancyService.deleteCover(vacancyId);
        return ResponseEntity.noContent().build();
    }
}
