package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageMapper {
    Stage toEntity(StageRequestCreateDto stageRequestCreateDto);

    StageRequestCreateDto toDto(Stage stage);
}