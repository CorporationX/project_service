package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.projectresource.ProjectResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectResourceMapper {

    @Mapping(target = "createdById", source = "createdBy",qualifiedByName = "getTeamMemberId")
    @Mapping(target = "projectId", source = "project", qualifiedByName = "getProjectId")
    ProjectResourceDto toDto(Resource resource);

    @Named("getTeamMemberId")
    default long getTeamMemberId(TeamMember teamMember){
        return teamMember.getId();
    }

    @Named("getProjectId")
    default long getProjectId(Project project){
        return project.getId();
    }
}
