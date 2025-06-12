package faang.school.projectservice.controller.project;

import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{projectId}/exists")
    public ResponseEntity<Void> checkProjectExists(@PathVariable @NotNull @Positive Long projectId) {
        projectService.checkProjectExists(projectId);
        return ResponseEntity.ok().build();
    }
}
