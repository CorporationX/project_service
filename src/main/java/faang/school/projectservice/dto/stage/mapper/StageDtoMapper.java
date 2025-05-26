package faang.school.projectservice.dto.stage.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StageDtoMapper {
    List<StageDto> ToStageDtoList(List<Stage> stageList);

    List<Stage> ToStageList(List<StageDto> stageList);

    StageDto ToStageDto(Stage stage);

    Stage ToStage(StageDto stageDto);
}
