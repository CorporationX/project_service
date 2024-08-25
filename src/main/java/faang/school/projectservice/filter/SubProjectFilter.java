package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {
    boolean isAcceptable(SubProjectDtoFilter goal);

    Stream<Project> apply(Stream<Project> goal, SubProjectDtoFilter filters);

}
