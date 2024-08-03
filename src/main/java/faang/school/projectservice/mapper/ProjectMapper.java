package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto entityToDto(Project project);

    Project dtoToEntity(ProjectDto dto);

    List<ProjectDto> entitiesToDtos(List<Project> projectList);
}