package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectMapper {

    @Mapping(target = "ownerId", source = "ownerId")
    SubProjectDto toDto(Project project);

    @Mapping(target = "ownerId", source = "ownerId")
    Project toModel(SubProjectDto subProjectDto);

    @Named("ownerTeamMember")
    default TeamMember ownerTeamMember(long ownerId) {
        return TeamMember.builder().id(ownerId).build();
    }
}
