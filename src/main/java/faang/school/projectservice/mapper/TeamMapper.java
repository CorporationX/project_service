package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.TeamDto;
import faang.school.projectservice.model.Team;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    TeamDto toDto(Team entity);
    Team toEntity(TeamDto dto);
}
