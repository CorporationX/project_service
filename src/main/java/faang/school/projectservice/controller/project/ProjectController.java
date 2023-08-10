package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/project")
public class ProjectController {
    private final ProjectService projectService;
      private final UserContext userContext;


    @PutMapping
    public UpdateProjectDto update(@Valid @RequestBody UpdateProjectDto dto) {
        return projectService.update(dto);
    }
  
    @PostMapping
    public ResponseProjectDto create(@Valid @RequestBody CreateProjectDto dto) {
        return projectService.create(dto, userContext.getUserId());
    }
}
