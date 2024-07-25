package faang.school.projectservice.service.project.subproject_filter;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.List;

public interface SubProjectFilter {

    boolean isApplecable(SubProjectFilterDto filter);

    List<Project> apply(List<Project> projects, SubProjectFilterDto filter);

}
