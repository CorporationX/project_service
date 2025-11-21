package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    @Mapping(target = "teamId", source = "team.id")
    TeamMemberDto toDto(TeamMember member);

    @Mapping(target = "team", ignore = true) // при полной конвертации team игнорируем
    TeamMember toEntity(TeamMemberDto dto);

    @Named("dtoToIdOnly")
    default TeamMember dtoToIdOnly(TeamMemberDto dto) {
        if (dto == null || dto.getId() == null) return null;
        TeamMember m = new TeamMember();
        m.setId(dto.getId());
        return m;
    }

    @Named("teamFromId")
    default Team teamFromId(Long id) {
        if (id == null) return null;
        Team t = new Team();
        t.setId(id);
        return t;
    }
}