package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface StageMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "stageRolesDto", source = "stageRoles")
    StageDto toDto(Stage stage);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "stageRoles", source = "stageRolesDto")
    Stage toEntity(StageDto stageDto);
}