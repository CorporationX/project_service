package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Project;
import org.mapstruct.Named;

import java.util.List;

public interface ProjectIdMapper {

    @Named("toListProjectId")
    default List<Long> toListProjectId(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }

    @Named("toListProject")
    default List<Project> toListProject(List<Long> projectIds) {
        return projectIds.stream().map(id -> {
            Project project = new Project();
            project.setId(id);
            return project;
        }).toList();
    }
}