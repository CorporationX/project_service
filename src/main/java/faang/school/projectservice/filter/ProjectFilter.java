package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

/**
 * ProjectFilter — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author fuckmynameagain
 * @since 14.08.2025
 */
public interface ProjectFilter {
    boolean isApplicable(ProjectDto projectDto);

    Stream<Project> apply(Stream<Project> projects, ProjectDto projectDto);
}