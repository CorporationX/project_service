package faang.school.projectservice.mapper.project.stage;

import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {StageRolesMapper.class})
public interface StageMapper {

    @Mapping(source = "projectId", target = "project.id")
    Stage toStage(StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    StageDto toStageDto(Stage stage);

    List<Stage> toStages(List<StageDto> stageDtos);

    List<StageDto> toStageDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "roles", target = "stageRoles")
    Stage toStage(StageCreateDto stageCreateDto);
}
