package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    @Mapping(target = "team", ignore = true)
    TeamMemberDto toDto(TeamMember teamMember);
}
