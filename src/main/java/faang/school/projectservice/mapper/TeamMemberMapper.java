package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeamMemberMapper {
    TeamMemberDto toDto(TeamMember teamMember);

    TeamMember toEntity(TeamMemberDto teamMemberDto);

    List<TeamMemberDto> toDtoList(List<TeamMember> teamMembers);

    List<TeamMember> toEntityList(List<TeamMemberDto> teamMemberDtos);
}
