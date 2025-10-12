package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageMapper {
    Stage toEntity(StageCreateDto stageCreateDto);

    StageCreateDto toDto(Stage stage);
}