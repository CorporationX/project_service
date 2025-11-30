package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.TeamSimpleDto;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamSimpleDto toDto(Team team);
    Team toEntity(TeamSimpleDto teamDto);
}
