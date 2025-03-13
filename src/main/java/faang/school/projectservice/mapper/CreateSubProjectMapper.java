package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreateSubProjectMapper {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "stages", ignore = true)
    Project toEntity(CreateSubProjectDto projectDto);

    @Mapping(target = "children", source = "children")
    @Mapping(target = "parentProject", source = "parentProject.id")
    @Mapping(target = "stages", source = "stages", qualifiedByName = "mapStagesToNames")
    CreateSubProjectDto toDto(Project project);

    default List<CreateSubProjectDto> mapProjectsToDtos(List<Project> children) {
        return children != null ? children.stream()
                .map(this::toDto)
                .toList()
                : null;
    }

    @Named("mapStagesToNames")
    default List<String> mapStagesToNames(List<Stage> stages) {
        return stages != null ? stages.stream()
                .map(Stage::getStageName)
                .toList()
                : null;
    }
}
