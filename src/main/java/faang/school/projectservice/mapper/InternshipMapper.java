package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "internsId", source = "interns", qualifiedByName = "listOfId")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    InternshipDto toDto(Internship internship);

    Internship toEntity(InternshipDto internshipDto);

    @Named("listOfId")
    default List<Long> listOfId(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getUserId)
                .toList();
    }
}
