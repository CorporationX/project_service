package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mappings({
            @Mapping(target = "mentorId", source = "mentor", qualifiedByName = "mentorToMentorId"),
            @Mapping(target = "projectId", source = "project.id"),
            @Mapping(source = "interns", target = "internsId", qualifiedByName = "toInternId")
    })
    InternshipDto toDto(Internship entity);

    @Mappings({
            @Mapping(target = "mentor", source = "mentorId", qualifiedByName = "mentorIdToMentor"),
            @Mapping(target = "project.id", source = "projectId"),
            @Mapping(target = "schedule.id", source = "scheduleId"),
            @Mapping(source = "internsId", target = "interns", qualifiedByName = "internIdToInterns")

    })
    Internship toEntity(InternshipDto dto);

    void update(InternshipDto dto, @MappingTarget Internship entity);

    @Named("mentorToMentorId")
    default long mentorToMentorId(TeamMember mentor) {
        return mentor.getId();
    }

    @Named("mentorIdToMentor")
    default TeamMember mentorIdToMentor(long mentorId) {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(mentorId);
        return teamMember;
    }

    @Named("toInternId")
    default List<Long> toInternId(List<TeamMember> interns) {
        if (interns == null) {
            return new ArrayList<>();
        }
        return interns.stream()
                .map(TeamMember::getUserId)
                .toList();
    }

    @Named("internIdToInterns")
    default List<TeamMember> internIdToInterns(List<Long> internsId) {
        return internsId.stream()
                .map(id -> {
                    TeamMember intern = new TeamMember();
                    intern.setId(id);
                    return intern;
                })
                .collect(Collectors.toList());
    }
}