package faang.school.projectservice.filter.project;

import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface DefaultProjectFilter {

    Stream<Project> apply(Stream<Project> projects);
}
