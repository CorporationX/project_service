package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto filters);

    void apply(List<Project> projects, ProjectFilterDto filters);
}
