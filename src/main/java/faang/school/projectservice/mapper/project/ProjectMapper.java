package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "taskIds", ignore = true)
    ProjectDto toDto(Project project);

    List<ProjectDto> toDtos(List<Project> projects);

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toEntity(ProjectDto projectDto);
}
