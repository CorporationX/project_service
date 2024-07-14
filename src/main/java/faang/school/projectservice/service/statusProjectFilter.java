package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class statusProjectFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return StringUtils.isBlank(filters.getProjectStatus());
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filters) {
        return projects
                .filter(project -> project.getStatus().toString()
                        .equals(filters.getProjectStatus()));
    }
}
