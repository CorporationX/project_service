package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.stagerole.StageRolesMapper;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {StageRolesMapper.class, StageMapperHelper.class})

public interface StageMapper {

    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "stageRolesDtosList", target = "stageRoles")
    Stage toEntity(StageDto dto);

    StageDto toDto(Stage entity);

    List<StageDto> toDtos(List<Stage> entities);

}
