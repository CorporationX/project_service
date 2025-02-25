package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponseDto save(@RequestBody ProjectCreateRequestDto projectDto) {
        log.info("#ProjectContoller: create request for project:[{}] has been received", projectDto);
        return projectService.save(projectDto);
    }

    @PutMapping("/{id}")
    public ProjectResponseDto update(@PathVariable Long id, @RequestBody ProjectUpdateRequestDto projectDto) {
        log.info("#ProjectContoller: request for updating project:[{}] with id: {} has been received", projectDto, id);
        return projectService.update(id, projectDto);
    }

    @GetMapping("/search")
    public List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter) {
        log.info("#ProjectController: request to find all projects matching the filter:[{}] has been received", filter);
        return projectService.findAllByFilter(filter);
    }

    @GetMapping
    public List<ProjectResponseDto> findAll() {
        log.info("#ProjectController: request to find all projects has been received");
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponseDto findById(@PathVariable Long id) {
        log.info("#ProjectContoller: request to find a project by its id:{} has been received", id);
        return projectService.findById(id);
    }

    @PostMapping("/{projectId}/presentation")
    public ProjectResponseDto generatePdf(@PathVariable Long projectId) {

        return projectService.creatingPresentation(projectId);
    }

    @GetMapping("/{projectId}/presentation/download")
    public ResponseEntity<InputStreamResource> downloadFile(@Valid @NotNull @PathVariable Long projectId) {

        S3ObjectDto obj = projectService.downloadPdf(projectId);
        InputStreamResource body = new InputStreamResource(obj.inputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + obj.fileName() + "\"." + obj.contentType())
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}
