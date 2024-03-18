package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    List<ProjectDto> toDtoList(List<Project> projectList);

    List<Project> toProjectList(List<ProjectDto> projectDtoList);

    void updateEntityFromDto(ProjectDto projectDto, @MappingTarget Project project);
}
