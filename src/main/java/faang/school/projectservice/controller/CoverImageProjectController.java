package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CoverImageFromAwsDto;
import faang.school.projectservice.dto.project.ProjectCoverImageDto;
import faang.school.projectservice.service.CoverImageProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/project/cover/")
public class CoverImageProjectController {
    private final CoverImageProjectService coverImageProjectService;
    private final UserContext userContext;

    @PostMapping
    public ProjectCoverImageDto upload(@RequestParam("file") MultipartFile file) {
        return coverImageProjectService.upload(file, userContext.getUserId());
    }

    @GetMapping("/{id}")
    public CoverImageFromAwsDto get(@PathVariable("id") Long projectId) throws IOException {
        return coverImageProjectService.getByProjectId(projectId);
    }

    @DeleteMapping("/{id}")
    public ProjectCoverImageDto delete(@PathVariable("id") Long projectId) {
        return coverImageProjectService.deleteByProjectId(projectId);
    }
}

