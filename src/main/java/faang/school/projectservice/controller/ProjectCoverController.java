package faang.school.projectservice.controller;

import faang.school.projectservice.dto.cover.CoverDto;
import faang.school.projectservice.mapper.cover.ProjectCoverMapper;
import faang.school.projectservice.service.CoverForProjectService.ProjectCoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}