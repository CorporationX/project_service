package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.service.project.ProjectViewService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectViewController {

    private final ProjectViewService projectViewService;
    private final UserContext userContext;

    @GetMapping("/view/{projectId}")
    public ProjectResponseDto viewProject(@Positive @PathVariable Long projectId) {
        long userId = userContext.getUserId();
        return projectViewService.viewProject(projectId, userId);
    }
}
