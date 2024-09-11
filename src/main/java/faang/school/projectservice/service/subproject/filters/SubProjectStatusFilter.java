package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectStatusFilter implements SubProjectFilter {

    @Override
    public boolean isApplicable(SubProjectFilterDto subProjectFilterDto) {
        return subProjectFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto subProjectFilterDto) {
        return projects
                .filter(project -> project.getStatus() == subProjectFilterDto.getStatus());
    }
}
