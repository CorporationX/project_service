package faang.school.projectservice.service.mapper;

import faang.school.projectservice.dto.InitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InitiativeMapper {
    InitiativeMapper INSTANCE = Mappers.getMapper(InitiativeMapper.class);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "curator.id", target = "curatorId")
    @Mapping(source = "stages", target = "stages")
    InitiativeDto toDto(Initiative initiative);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "curatorId", target = "curator.id")
    @Mapping(source = "stages", target = "stages")
    Initiative toEntity(InitiativeDto initiativeDto);
}
