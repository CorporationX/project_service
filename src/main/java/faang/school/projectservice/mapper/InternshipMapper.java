package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    //@Mapping(target = "internsId", source = "interns", qualifiedByName = "mapInternsToId")
    @Mapping(target = "mentorId", source = "mentor", qualifiedByName = "mapMentorToId")
    @Mapping(target = "projectId", source = "project.id")
    InternshipDto toDto(Internship internship);

    Internship toEntity(InternshipDto internshipDto);

    @Named("mapInternsToId")
    default List<Long> mapInterns(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getUserId)
                .toList();
    }

    @Named("mapMentorToId")
    default Long mapMentors(TeamMember teamMember) {
        return teamMember.getId();
    }
}
