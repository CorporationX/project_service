package faang.school.projectservice.mapper.stages;

import faang.school.projectservice.dto.stages.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StageMapper {

    Stage toEntity(StageDto stageDto);

    StageDto toDto(Stage stage);
}
