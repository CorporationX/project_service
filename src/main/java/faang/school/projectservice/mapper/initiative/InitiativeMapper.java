package faang.school.projectservice.mapper.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.initiative.Initiative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {InitiativeMapperHelper.class, StageMapper.class})
public interface InitiativeMapper {

    @Mapping(source = "curator.id", target = "curatorId")
    InitiativeDto toDto(Initiative entity);

    @Mapping(source = "curatorId", target = "curator")
    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "stageDtoList", target = "stages")
    Initiative toEntity(InitiativeDto initiativeDto);

    List<InitiativeDto> toDtos(List<Initiative> entities);
}
