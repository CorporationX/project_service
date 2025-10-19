package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.CreateStageDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageMapper {
    Stage toStage(StageDto dto);

    void updateStage(UpdateStageDto dto, @MappingTarget Stage stage);

    StageDto toStageDto(Stage stage);
    List<StageDto> toListStageDto(List<Stage> stage);
    List<Stage> toListStage(List<StageDto> stage);

}
