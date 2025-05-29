package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StageDtoMapper {
    List<StageDto> toStageDtoList(List<Stage> stageList);

    List<Stage> toStageList(List<StageDto> stageList);

    StageDto toStageDto(Stage stage);

    Stage toStage(StageDto stageDto);
}
