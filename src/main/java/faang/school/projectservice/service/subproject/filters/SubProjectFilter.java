package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface SubProjectFilter {
    public boolean isApplicable(SubProjectFilterDto subProjectFilterDto);

    Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto subProjectFilterDto);
}
