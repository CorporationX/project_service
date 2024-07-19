package faang.school.projectservice.filter.subprojectfilter;

import faang.school.projectservice.dto.filter.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {
    boolean isApplicable(SubProjectFilterDto filter);

    Stream<Project> apply(Stream<Project> subProjects, SubProjectFilterDto filter);
}
