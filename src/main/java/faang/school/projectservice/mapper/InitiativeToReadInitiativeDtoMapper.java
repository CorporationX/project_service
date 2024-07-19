package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.initiative.ReadInitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InitiativeToReadInitiativeDtoMapper {

     ReadInitiativeDto map(Initiative event);
}
