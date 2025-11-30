package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.StageSimpleDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageMapper {
    StageSimpleDto toDto(Stage stage);
    Stage toEntity(StageSimpleDto stageDto);
}
