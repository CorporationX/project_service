package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.TeamMemberDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);

    TeamMemberDto toDto(TeamMember teamMember);

}
