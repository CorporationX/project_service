package faang.school.projectservice.mapper.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InitiativeMapper {
    InitiativeDto toDto(Initiative initiative);

    Initiative toEntity(InitiativeDto initiativeDto);
}
