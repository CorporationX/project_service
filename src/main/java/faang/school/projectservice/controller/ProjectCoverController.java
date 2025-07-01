package faang.school.projectservice.controller;

import faang.school.projectservice.dto.cover.CoverDto;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.mapper.cover.ProjectCoverMapper;
import faang.school.projectservice.service.CoverForProjectService.ProjectCoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectCoverController {

    private final ProjectCoverService projectCoverService;
    private final ProjectCoverMapper projectCoverMapper;

    @PostMapping("/{projectId}/cover")
    @ResponseStatus(HttpStatus.CREATED)
    public CoverDto uploadCover(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {

        return projectCoverMapper.toDto(
                projectCoverService.addImageToProject(projectId, file)
        );
    }

    @DeleteMapping("/{projectId}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCover(@PathVariable Long projectId) {
        projectCoverService.removeCoverFromProjectById(projectId);
    }


    @GetMapping("/{projectId}/cover")
    public ResponseEntity<InputStreamResource> getProjectCover(@PathVariable Long projectId) throws IOException {
        S3FileDto fileDto = projectCoverService.getProjectCoverFile(projectId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileDto.getContentType()));
        headers.setContentLength(fileDto.getContentLength());
        headers.setContentDispositionFormData("attachment", fileDto.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileDto.getResource().getInputStream()));
    }
}