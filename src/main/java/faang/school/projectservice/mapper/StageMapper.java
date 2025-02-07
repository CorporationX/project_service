package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageMapper {
    Stage toEntity(StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.ownerId", target = "userId")
    StageDto toDto(Stage stage);

}
