package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "owner", source = "owner.id")
    ProjectDto toDto(Project entity);

    @Mapping(target = "owner", source = "owner", qualifiedByName = "mapTeamMember")
    Project toEntity(ProjectDto dto);

    @Named("mapTeamMember")
    default TeamMember mapTeamMember(Long ownerId) {
        return TeamMember.builder().id(ownerId).build();
    }
}