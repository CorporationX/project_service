package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(source = "stageRolesDtos", target = "stageRoles")
    Stage toStageEntity(StageCreateDto stageCreateDto);

    List<StageDto> toStageDtos(List<Stage> stages);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "stageRoles", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toStageEntity(StageUpdateDto stageUpdateDto);

    StageDto toStageDto(Stage stage);
}
