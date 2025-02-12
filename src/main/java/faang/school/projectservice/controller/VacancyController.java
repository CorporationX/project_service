package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ImageType;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/vacancy/{id}/cover")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {

    private static final List<ImageType> IMAGE_TYPES = List.of(ImageType.png, ImageType.jpg);

    private final VacancyService vacancyService;
    private final UserContext userContext;

    @PostMapping
    @Operation(summary = "Add cover to vacancy", description = "Allows you to add cover for vacancy")
    public void addCover(@PathVariable Long id, @RequestBody MultipartFile file) {
        checkContentType(file);
        vacancyService.addCover(id, file);
    }

    @GetMapping
    @Operation(summary = "Download cover", description = "Allows you to download cover from vacancy")
    public InputStream getVacancyCover(@PathVariable Long id) {
        return vacancyService.getVacancyCover(id);
    }

    @DeleteMapping
    @Operation(summary = "Delete vacancy cover", description = "Allows you to delete vacancy cover")
    public void deleteVacancyCover(@PathVariable Long id) {
        vacancyService.deleteVacancyCover(id, userContext.getUserId());
    }

    private void checkContentType(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File is empty.");
        }
        String fileType = Objects.requireNonNull(file.getContentType()).substring(file.getContentType()
                .lastIndexOf("/") + 1);
        if (IMAGE_TYPES.stream().noneMatch(imageType -> Objects.equals(imageType.toString(), fileType))) {
            log.error("Invalid file type.");
            throw new DataValidationException("Invalid file type.");
        }
    }

}
