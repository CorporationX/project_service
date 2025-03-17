package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface StageMapper {
    @Mapping(target = "roleAndCount", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "id", source = "stageId")
    StageDtoCreate toDto(Stage entity);

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "stageRoles", ignore = true)
    @Mapping(target = "project.id", ignore = true)
    @Mapping(target = "stageId", source = "id")
    Stage toEntity(StageDtoCreate dto);

    List<StageDtoCreate> toDtoList(List<Stage> entities);

    List<Stage> toEntityList(List<StageDtoCreate> dtos);

}
