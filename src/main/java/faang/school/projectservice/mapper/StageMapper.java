package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(target = "projectId", source = "project.id")
    StageDto toDto(Stage stage);

    Stage toEntity(StageDto stageDto);

    List<StageDto> toDto(List<Stage> list);

    List<Stage> toEntity(List<StageDto> list);
}
