package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectFilter {
    boolean isApplicable(ProjectDtoFilter projectDtoFilter);



    void apply(List<Project> projects, ProjectDtoFilter projectDtoFilter);
}
