package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/project")
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping
    public UpdateProjectDto update(@Valid @RequestBody UpdateProjectDto dto) {
        return projectService.update(dto);
    }
}
