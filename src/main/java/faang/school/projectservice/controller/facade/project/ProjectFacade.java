package faang.school.projectservice.controller.facade.project;

import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectFacade {
    private final ProjectService projectService;
}
