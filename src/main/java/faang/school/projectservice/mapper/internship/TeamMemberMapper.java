package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    TeamMemberDto toTeamMemberDto(TeamMember teamMember);

    TeamMember toEntity(TeamMemberDto teamMemberDto);
}
