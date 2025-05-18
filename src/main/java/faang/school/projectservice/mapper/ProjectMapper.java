package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project toProjectEntity(ProjectDto projectDto);

    ProjectDto toProjectDto(Project project);

    List<Project> toProjectEntityList(List<ProjectDto> projectDtoList);

    List<ProjectDto> toProjectDtoList(List<Project> projectList);
}
