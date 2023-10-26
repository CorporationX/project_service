package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    @Mapping(target = "teamId", source = "team.id")
    TeamMemberDto toDto(TeamMember member);

    @Mapping(target = "team.id", source = "teamId")
    TeamMember toEntity(TeamMemberDto teamMemberDto);
}
