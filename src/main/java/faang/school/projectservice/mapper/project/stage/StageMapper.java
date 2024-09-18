package faang.school.projectservice.mapper.project.stage;

import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageRoleDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "projectId", target = "project.id")
    Stage toStage(StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    StageDto toStageDto(Stage stage);

    List<Stage> toStages(List<StageDto> stageDtos);

    List<StageDto> toStageDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    Stage toStage(StageCreateDto stageCreateDto);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(target = "executors", ignore = true)
    Stage toStage(StageUpdateDto stageUpdateRemoveDto);

    @Mapping(source = "stageId", target = "stage.stageId")
    StageRoles toStageRoles(StageRoleDto stageRoleDto, @Context StageRepository stageRepository);

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRoleDto toStageRoleDto(StageRoles stageRoles);

    List<StageRoles> toStageRoles(List<StageRoleDto> stageRoleDtos);

    List<StageRoleDto> toStageRoleDtos(List<StageRoles> stageRoles);
}
