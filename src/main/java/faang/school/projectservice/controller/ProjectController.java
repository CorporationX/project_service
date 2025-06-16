package faang.school.projectservice.controller;

import faang.school.projectservice.exception.ProjectImageCoverException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/{projectId}/cover-image")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCoverImage(
            @PathVariable Long projectId,
            @RequestParam("image") MultipartFile file
    ) {
        log.debug("request add project: projectId: {}", projectId);
        projectService.addCoverImage(projectId, file);
    }

    @DeleteMapping("/{projectId}/cover-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoverImage(@PathVariable Long projectId) {
        log.debug("request delete project cover image: projectId: {}", projectId);
        projectService.deleteCoverImage(projectId);
    }
}
