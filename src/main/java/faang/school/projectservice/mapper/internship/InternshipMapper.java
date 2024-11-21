package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    @Mapping(source = "interns", target = "internsIds", qualifiedByName = "map")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "project.id", target = "projectId")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toListDto(List<Internship> internship);

    @Mapping(source = "mentorId", target = "mentorId", qualifiedByName = "toTeamMember")
    Internship toEntity(InternshipDto internshipDto);

    @Named("toTeamMember")
    default TeamMember toTeamMember(Long mentorId) {
        if (mentorId == null) {
            return null;
        }

        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        return mentor;
    }

    @Named("map")
    default List<Long> map(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }
}
