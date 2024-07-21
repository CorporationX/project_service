package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.service.project.ProjectCoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/cover")
@Tag(name = "Project Cover")
public class ProjectCoverController {
    private final ProjectCoverService projectCoverService;

    @Operation(
            summary = "Save new cover for project",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @PostMapping("/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectCoverDto save(@Positive @PathVariable long projectId,
                                @NotNull @RequestParam("file") MultipartFile file) {
        return projectCoverService.save(projectId, file);
    }

    @Operation(
            summary = "Get project's cover image by id",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @GetMapping("/{projectId}")
    public InputStreamResource getCover(@Positive @PathVariable long projectId) {
        return projectCoverService.getByProjectId(projectId);
    }

    @Operation(
            summary = "Change project's cover image by id",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @PatchMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectCoverDto changeCover(@Positive @PathVariable long projectId,
                                       @RequestBody MultipartFile file) {
        return projectCoverService.changeProjectCover(projectId, file);
    }


    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "Delete project cover",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    public ProjectCoverDto deleteProjectCover(@Positive @Parameter @PathVariable long projectId) {
        return projectCoverService.deleteByProjectId(projectId);
    }
}
