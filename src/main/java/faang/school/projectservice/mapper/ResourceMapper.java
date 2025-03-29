package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapTeamMemberToId")
    @Mapping(source = "updatedBy", target = "updatedBy", qualifiedByName = "mapTeamMemberToId")
    ResourceDto toResource(Resource resource);

    @Named("mapTeamMemberToId")
    default Long mapTeamMemberToId(TeamMember teamMember) {
        return teamMember != null ? teamMember.getId() : null;
    }
}
