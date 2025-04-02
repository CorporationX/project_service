package faang.school.projectservice.controller;

import faang.school.projectservice.dto.presentation.PresentationRequestDto;
import faang.school.projectservice.dto.presentation.PresentationFilterDto;
import faang.school.projectservice.dto.presentation.PresentationUpdateDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.service.project.ProjectService;
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
public class PresentationController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponseDto save(@RequestBody PresentationRequestDto projectDto) {
        log.info("#ProjectContoller: create request for project:[{}] has been received", projectDto);
        return projectService.save(projectDto);
    }

    @PostMapping("/{projectId}/presentation")
    public void generatePdf(@PathVariable Long projectId) {
        projectService.createPresentation(projectId);
    }

    @PutMapping("/{id}")
    public ProjectResponseDto update(@PathVariable Long id, @RequestBody PresentationUpdateDto projectDto) {
        log.info("#ProjectContoller: request for updating project:[{}] with id: {} has been received", projectDto, id);
        return projectService.update(id, projectDto);
    }

    @GetMapping("/search")
    public List<ProjectResponseDto> findAllByFilter(PresentationFilterDto filter) {
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

    @GetMapping("/{projectId}/presentation/download")
    public ResponseEntity<InputStreamResource> downloadFile(@Valid @NotNull @PathVariable Long projectId) {
        S3ObjectDto obj = projectService.downloadPdf(projectId);
        InputStreamResource body = obj.inputStream();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + obj.fileName() + "\"." + obj.contentType())
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}
