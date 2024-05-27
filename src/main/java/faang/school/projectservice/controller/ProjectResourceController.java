package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/projects/resources")
@RequiredArgsConstructor
@Tag(name = "Project Resources")
public class ProjectResourceController {

    @PostMapping("/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new file to project")
    public ResourceDto saveFiles(@Positive @PathVariable long projectId,
                                 @NotNull @RequestParam("file") MultipartFile file) {
        return null;
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project files by id")
    public InputStream getFile(@Positive @PathVariable long projectId,
                               @NotNull @RequestParam("key") String key) {
        return null;
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete project file")
    public String deleteFile(@Positive @PathVariable long userId,
                             @NotNull @RequestParam("key") String key) {
        return null;
    }
}
