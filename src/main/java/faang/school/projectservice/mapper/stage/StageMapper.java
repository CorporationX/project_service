package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface StageMapper {

    @Mapping(target = "project.id", source = "projectId")
    Stage toStageEntity(StageDto stageDto);

    @Mapping(target = "projectId", source = "project.id")
    StageDto toStageDto(Stage stage);
}
