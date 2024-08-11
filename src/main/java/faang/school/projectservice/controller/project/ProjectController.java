package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService service;

    @GetMapping("/project/{projectId}")
    public ProjectDto getProject(@PathVariable long projectId) {
        return service.getProject(projectId);
    }

    @PostMapping("/projects")
    public List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids) {
        return service.getProjectsByIds(ids);
    }

    @PutMapping("/project/{projectId}/covers")
    public ResponseEntity<ProjectCoverDto> addCover(@PathVariable("projectId")
                                                    @Min(value = 1, message = "Must be more then 0.")
                                                    long id,
                                                    @RequestBody MultipartFile file) {
        return ResponseEntity.ok(service.addCover(id, file));
    }
}