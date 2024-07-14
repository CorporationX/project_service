package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface StageMapper {

    @Mapping(source = "projectId", target = "project.id")

    @Mapping(target = "stageRoles", ignore = true)
    Stage toEntity(StageDto dto);

    StageDto toDto(Stage entity);

    List<StageDto> toDtos(List<Stage> entities);

}
