
package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project fromDto(ProjectDto project);
    ProjectDto toDto(Project project);

    List<Project> fromDto(List<ProjectDto> project);

    List<ProjectDto> toDto(List<Project> project);
}
