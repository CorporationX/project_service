package faang.school.projectservice.dto.stage.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface StageDtoMapper {
    List<StageDto> stageListToStageDtoList(List<Stage> stageList);

    List<Stage> stageDtoListToStageList(List<StageDto> stageList);

    StageDto stageToStageDTO(Stage stage);

    Stage stageDtoToStage(StageDto stageDto);
}
