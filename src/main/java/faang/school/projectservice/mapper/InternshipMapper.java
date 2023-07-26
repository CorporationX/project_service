package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;


import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "interns", source = "interns", qualifiedByName = "listOfId")
    InternshipDto toDto(Internship internship);

    Internship toEntity(InternshipDto internshipDto);

    @Named("listOfId")
    default List<Long> listOfId(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
}