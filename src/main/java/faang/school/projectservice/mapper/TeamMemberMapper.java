package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.intership.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    TeamMember toEntity(TeamMemberDto teamMemberDto);
}
