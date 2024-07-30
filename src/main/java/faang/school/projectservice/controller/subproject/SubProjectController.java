package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;
    private final MomentRepository momentRepository;

    @PostMapping("/subProjects")
    public ResponseEntity<ProjectDto> createSubProject(@Validated @RequestBody CreateSubProjectDto subProjectDto) {
        ProjectDto createdProject = projectService.createSubProject(subProjectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/subProjects/{subProjectId}")
    public ResponseEntity<ProjectDto> updateSubProject(
            @PathVariable Long subProjectId,
            @Validated @RequestBody UpdateSubProjectDto updateSubProjectDto) {
        return ResponseEntity.ok(projectService.updateSubProject(subProjectId, updateSubProjectDto));
    }

    @PostMapping("/{projectId}/subProjects")
    public ResponseEntity<List<ProjectDto>> getFilteredSubProjects(
            @PathVariable Long projectId,
            @Validated @RequestBody FilterSubProjectDto filters) {
        return ResponseEntity.ok(projectService.getFilteredSubProjects(projectId, filters));
    }
}
