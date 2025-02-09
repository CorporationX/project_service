package faang.school.projectservice.mapper;

import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import faang.school.projectservice.dto.client.StageDto;

@Mapper(componentModel = "spring")
public interface StageMapper {

    @Mapping(target = "id", source = "stageId")
    StageDto toDto(Stage stage);

    @Mapping(target = "stageId", source = "id")
    Stage toEntity(StageDto stageDto);
}