package faang.school.projectservice.filter;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectOwnerFilter implements DefaultProjectFilter {

    private final UserContext userContext;

    @Override
    public Stream<Project> apply(Stream<Project> projects) {
        return projects.filter(project -> {
            if (project.getOwnerId() == null) {
                return false;
            }
            return project.getOwnerId().equals(userContext.getUserId());
        });
    }
}
