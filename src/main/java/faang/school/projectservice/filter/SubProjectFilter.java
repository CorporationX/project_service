package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SubProjectFilter {

    boolean isApplicable(SubProjectFilterDto filter);

    void apply(List<ProjectDto> projects, SubProjectFilterDto filter);
}
