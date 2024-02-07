package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);

    Stream<Project> applyFilter(ProjectFilterDto projectFilterDto, Stream<Project> projects);
}
