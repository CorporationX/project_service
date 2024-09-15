package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageMapper {
    StageDto toDto(Stage stage);
    Stage toEntity(StageDto stageDto);
}
