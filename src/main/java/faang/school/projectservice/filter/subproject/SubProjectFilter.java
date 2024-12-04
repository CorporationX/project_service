package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.project.Project;

import java.util.List;

public interface SubProjectFilter {

    boolean isApplicable(SubProjectFilterDto subProjectFilterDto);

    List<Project> apply(List<Project> projects,
                                      SubProjectFilterDto subProjectFilterDto);

}
