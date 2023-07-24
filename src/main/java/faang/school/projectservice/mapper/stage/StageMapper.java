package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.client.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageRolesMapper.class, injectionStrategy = InjectionStrategy.FIELD)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    StageDto toDto(Stage stage);

    @Mapping(source = "projectId", target = "project.id")
    Stage toEntity(StageDto stageDto);
}
