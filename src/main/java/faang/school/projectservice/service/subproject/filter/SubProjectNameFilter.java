package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameFilter implements SubProjectFilter {

    @Override
    public boolean isApplicable(SubProjectFilterDto subProjectFilterDto) {
        return subProjectFilterDto.namePattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> subProjects, SubProjectFilterDto subProjectFilterDto) {
        return subProjects.filter(project -> project.getName().matches(subProjectFilterDto.namePattern()));
    }
}