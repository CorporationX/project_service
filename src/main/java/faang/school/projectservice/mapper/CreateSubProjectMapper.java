package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreateSubProjectMapper extends BaseProjectMapper {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "stages", ignore = true)
    Project toEntity(CreateSubProjectDto projectDto);

    @Mapping(target = "children", source = "children", qualifiedByName = "mapProjectsToIds")
    @Mapping(target = "parentProject", source = "parentProject.id")
    @Mapping(target = "stages", source = "stages", qualifiedByName = "mapStagesToNames")
    CreateSubProjectDto toDto(Project project);
}
