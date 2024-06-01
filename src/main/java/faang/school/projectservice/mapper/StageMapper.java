package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "stageRolesDto", target = "stageRoles")
    @Mapping(source = "projectId", target = "project.id")
    Stage toEntity(StageDto stageDto);

    @Mapping(source = "stageRoles", target = "stageRolesDto")
    @Mapping(source = "project.id", target = "projectId")
    StageDto toDto(Stage stage);
}
