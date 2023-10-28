package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    @Mapping(target = "projectId", source = "project.id")
    TeamDto toDto(Team team);

    @Mapping(target = "project.id", source = "projectId")
    Team toEntity(TeamDto dto);
}
