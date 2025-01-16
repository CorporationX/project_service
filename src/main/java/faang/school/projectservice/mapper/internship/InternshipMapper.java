package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "schedule.id", target = "scheduleId")
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "map")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toDto(List<Internship> interns);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "scheduleId", target = "schedule.id")
    Internship toEntity(InternshipDto internshipDto);

    @Named("map")
    default List<Long> map(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }
}
