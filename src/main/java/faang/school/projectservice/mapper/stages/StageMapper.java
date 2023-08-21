package faang.school.projectservice.mapper.stages;

import faang.school.projectservice.dto.stages.StageDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StageMapper {

    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);

    Stage toEntity(StageDto stageDto);
    StageDto toDto(Stage stage);
}
