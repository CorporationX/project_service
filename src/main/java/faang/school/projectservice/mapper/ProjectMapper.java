package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper extends BaseProjectMapper {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "stages", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "children", source = "children", qualifiedByName = "mapProjectsToIds")
    @Mapping(target = "stages", source = "stages", qualifiedByName = "mapStagesToIds")
    ProjectDto toDto(Project project);
}
