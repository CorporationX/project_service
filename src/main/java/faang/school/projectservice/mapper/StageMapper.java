package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageMapper {
    @Mapping(source = "", target = "")
    Stage toEntity(StageRequestCreateDto stageRequestCreateDto);

    StageRequestCreateDto toDto(Stage stage);
}