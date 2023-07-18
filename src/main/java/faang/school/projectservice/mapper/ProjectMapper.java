package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    ProjectDto toDto(Project project);

    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "ownerTeamMember")
    Project toModel(ProjectDto projectDto);

    @Named("ownerTeamMember")
    default TeamMember ownerTeamMember(long ownerId) {
        return TeamMember.builder().id(ownerId).build();
    }
}
