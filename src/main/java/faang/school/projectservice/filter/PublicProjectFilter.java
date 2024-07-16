package faang.school.projectservice.filter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PublicProjectFilter implements DefaultProjectFilter {

    @Override
    public Stream<Project> apply(Stream<Project> projects) {
        return projects.filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC));
    }
}
