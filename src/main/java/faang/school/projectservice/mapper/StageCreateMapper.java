package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StageCreateMapper {
    @Mapping(target = "roleAndCount", ignore = true)
    @Mapping(target = "id", source = "stageId")
    StageDtoCreate toDto(Stage entity);

    @Mapping(target = "stageId", source = "id")
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "stageRoles", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toEntity(StageDtoCreate dto);

    List<StageDtoCreate> toDtoList(List<Stage> entities);

    List<Stage> toEntityList(List<StageDtoCreate> dtos);

}
