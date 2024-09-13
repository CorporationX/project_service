package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRolesDto")
    StageDto toDto(Stage stage);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "stageRolesDto", target = "stageRoles")
    Stage toEntity(StageDto stageDto);

    List<StageDto> toStageDtos(List<Stage> stageRoles);
}
