package faang.school.projectservice.facade;

import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectFacade {
    private final ProjectService projectService;
}
