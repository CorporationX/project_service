package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.PROJECTS_PATH)
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;
    
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) {
        projectDto.setOwnerId(userContext.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectDto));
    }
}
