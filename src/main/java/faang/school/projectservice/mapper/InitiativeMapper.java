package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {InitiativeMapperHelper.class})
public interface InitiativeMapper {

    @Mapping(source = "curator.id", target = "curatorId")
    InitiativeDto toDto(Initiative entity);

    @Mapping(source = "curatorId", target = "curator")
    @Mapping(source = "projectId", target = "project")
    Initiative toEntity(InitiativeDto initiativeDto);

    List<InitiativeDto> toDtos(List<Initiative> entities);
}
