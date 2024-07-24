package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subProjects")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createSubProject(@Validated @RequestBody CreateSubProjectDto subProjectDto) {
        ProjectDto createdProject = projectService.createSubProject(subProjectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PostMapping("/update")
    public ResponseEntity<ProjectDto> updateSubProject(@Validated @RequestBody SubProjectUpdateDto subProjectUpdateDto) {
        ProjectDto updatedProject = projectService.updateSubProject(subProjectUpdateDto);
        return ResponseEntity.ok(updatedProject);
    }
}
