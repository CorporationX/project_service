package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = TeamMemberMapper.class)
public interface TeamMapper {

    @Mapping(target = "members", source = "teamMembers")
    TeamDto toDto(Team team);

    List<TeamDto> toDtoList(List<Team> teams);
}
