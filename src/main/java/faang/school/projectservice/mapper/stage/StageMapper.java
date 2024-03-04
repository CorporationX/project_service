package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {StageRolesMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StageMapper {
    @Mapping(target = "stageId", source = "id")
    @Mapping(target = "stageName", source = "name")
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "stageRoles", source = "roles")
    Stage toEntity(StageDto stageDto);

    @Mapping(target = "id", source = "stageId")
    @Mapping(target = "name", source = "stageName")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "roles", source = "stageRoles")
    StageDto toDto(Stage stage);
}
