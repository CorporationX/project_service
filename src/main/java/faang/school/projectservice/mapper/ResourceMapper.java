package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.CreateResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Named("longToBigInteger")
    static BigInteger longToBigInteger(long size) {
        return BigInteger.valueOf(size);
    }

    @Mapping(source = "originalFilename", target = "name")
    @Mapping(source = "uploadedKey", target = "key")
    @Mapping(source = "size", target = "size", qualifiedByName = "longToBigInteger")
    @Mapping(source = "teamMemberId", target = "createdBy", qualifiedByName = "mapTeamMember")
    @Mapping(source = "teamMemberId", target = "updatedBy", qualifiedByName = "mapTeamMember")
    @Mapping(source = "projectId", target = "project", qualifiedByName = "mapProject")
    @Mapping(source = "contentType", target = "type", qualifiedByName = "convertContentType")
    @Mapping(target = "allowedRoles", expression = "java(getDefaultRoles(dto.teamMemberId()))")
    @Mapping(target = "status", constant = "ACTIVE")
    Resource toResource(CreateResourceDto dto);

    @Named("mapTeamMember")
    default TeamMember mapTeamMember(Long teamMemberId) {
        if (teamMemberId == null) {
            return null;
        }
        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberId);
        return teamMember;
    }

    @Named("mapProject")
    default Project mapProject(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    @Named("convertContentType")
    default ResourceType convertContentType(String contentType) {
        return ResourceType.getResourceType(contentType);
    }

    default List<TeamRole> getDefaultRoles(Long teamMemberId) {
        return TeamRole.getAll();
    }
}
