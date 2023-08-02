package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageRolesMapper.class)
public interface StageMapper {

    StageDto toDto(Stage stage);

    Stage toEntity(StageDto stageDto);
}
