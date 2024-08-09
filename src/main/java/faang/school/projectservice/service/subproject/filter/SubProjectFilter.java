package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {
    boolean isApplicable(FilterSubProjectDto filters);

    Stream<Project> apply(Stream<Project> events, FilterSubProjectDto filters);
}
