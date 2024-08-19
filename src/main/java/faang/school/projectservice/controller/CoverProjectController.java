package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CoverFromStorageDto;
import faang.school.projectservice.dto.ProjectCoverDto;
import faang.school.projectservice.service.CoverProjectService;
import faang.school.projectservice.validator.ControllerValidator;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/{projectId}/cover")
public class CoverProjectController {
    public static final long MAX_IMAGE_SIZE = 5242880L;

    private static final long MIN_ID = 0L;
    private final ControllerValidator validator;
    private final CoverProjectService service;

    @PostMapping
    public ProjectCoverDto uploadProjectCover(
            @PathVariable @Min(MIN_ID) Long projectId,
            @RequestParam MultipartFile cover) {
        validator.validateMaximumSize(cover.getSize(), MAX_IMAGE_SIZE);
        return service.uploadProjectCover(projectId, cover);
    }

    @PutMapping
    public ProjectCoverDto changeProjectCover(
            @PathVariable @Min(MIN_ID) Long projectId,
            @RequestParam MultipartFile cover) {
        validator.validateMaximumSize(cover.getSize(), MAX_IMAGE_SIZE);
        return service.changeProjectCover(projectId, cover);
    }

    @GetMapping
    public ResponseEntity<byte[]> getProjectCover(
            @PathVariable @Min(MIN_ID) Long projectId) throws IOException {
        CoverFromStorageDto projectCoverDto = service.getProjectCover(projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(projectCoverDto.getCover(), headers, HttpStatus.OK);
    }

    @DeleteMapping
    public void deleteProjectImage(@PathVariable @Min(MIN_ID) Long projectId) {
        service.deleteProjectCover(projectId);
    }
}
