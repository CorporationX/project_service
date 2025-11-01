
package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toProject(CreateProjectDto projectDto);

    void update(UpdateProjectDto updateProjectDto, @MappingTarget Project project);

    ProjectDto toProjectDto(Project project);
}
