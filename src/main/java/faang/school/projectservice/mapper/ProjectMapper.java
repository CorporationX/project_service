package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.project.CreateProjectDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.UpdateProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project toProject(CreateProjectDto projectDto);

    void update(UpdateProjectDto projectDto, @MappingTarget Project entity);

    ProjectDto toProjectDto(Project project);

    Project toProject(ProjectDto projectDto);
}