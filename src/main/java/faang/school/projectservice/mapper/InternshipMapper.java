package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InternshipMapper {
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Internship toInternship(InternshipDto internshipDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "schedule.id", target = "scheduleId")
    @Mapping(target = "internsId", expression = "java(mapTeamMemberToIds(internship.getInterns()))")
    InternshipDto toInternshipDto(Internship internship);

    default List<Long> mapTeamMemberToIds(List<TeamMember> teamMember) {
        return teamMember != null ? teamMember.stream()
                .map(TeamMember::getId)
                .toList()
                : Collections.emptyList();
    }
}
