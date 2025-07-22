package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.util.project.ProjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class ProjectVisibilityFilter implements Filter<Project, ProjectFilterDto> {
    private final UserContext userContext;

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return true;
    }

    @Override
    public Stream<Project> filter(Stream<Project> entities, ProjectFilterDto dto) {
       return entities
               .filter(project -> ProjectUtil.isAvailable(project, userContext.getUserId()));
    }

}
