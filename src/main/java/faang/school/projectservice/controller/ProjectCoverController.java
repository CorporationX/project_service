package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.ProjectCover.ProjectCoverService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/projects")
@Validated
public class ProjectCoverController {

    private final ProjectCoverService projectCoverService;
    private final UserContext userContext;

    @PostMapping("/{projectId}/project-covers")
    public ResponseEntity<Void> addProjectCover(@PathVariable Long projectId, @NotNull @RequestBody MultipartFile file) {
        projectCoverService.addCover(projectId, userContext.getUserId(), file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{projectId}/project-covers", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadProjectCover(@PathVariable Long projectId) {

        return new ResponseEntity<>(projectCoverService.downloadCover(projectId), HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/project-covers")
    public ResponseEntity<Void> deleteProjectCover(@PathVariable Long resourceId) {
        projectCoverService.deleteCover(resourceId, userContext.getUserId());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{projectId}/project-covers")
    public ResponseEntity<Void> updateProjectCover(@PathVariable Long resourceId, @RequestBody MultipartFile file) {
        projectCoverService.updateCover(resourceId, userContext.getUserId(), file);

        return ResponseEntity.ok().build();
    }
}