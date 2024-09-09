package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.mapper.util.StageMapperUtil;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageMapperUtil.class)
public interface StageMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "rolesMap", source = "stageRoles", qualifiedByName = {"StageMapperUtil", "mapRoles"})
    @Mapping(target = "executorsIds", source = "executors", qualifiedByName = {"StageMapperUtil", "toExecutorsIds"})
    StageDto toDto(Stage stage);

    @Mapping(target = "project", source = "projectId", qualifiedByName = {"StageMapperUtil", "getProjectById"})
    @Mapping(target = "stageRoles", source = "rolesMap", qualifiedByName = {"StageMapperUtil", "getStageRolesByIds"})
    @Mapping(target = "executors", source = "executorsIds", qualifiedByName = {"StageMapperUtil", "getExecutorsByIds"} )
    Stage toStage(StageDto stageDto);

    List<StageDto> toDtoList(List<Stage> stages);

}
