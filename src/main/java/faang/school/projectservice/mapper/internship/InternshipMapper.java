package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.*;
import faang.school.projectservice.model.*;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "schedule.id", target = "scheduleId")
    @Mapping(source = "interns", target = "interns", qualifiedByName = "mapToId")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toDto(List<Internship> interns);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "scheduleId", target = "schedule.id")
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipDto internshipDto);

    @Named("mapToId")
    default List<InternshipUserInformationDto> mapToId(List<TeamMember> interns) {
        List<InternshipUserInformationDto> internshipUserInformationDtos = new ArrayList<>();
        interns.forEach(teamMember -> {
            InternshipUserInformationDto internshipUserInformationDto = new InternshipUserInformationDto();
            internshipUserInformationDto.setId(teamMember.getId());
            internshipUserInformationDto.setUserId(teamMember.getUserId());
            internshipUserInformationDto.setNickname(teamMember.getNickname());
            internshipUserInformationDtos.add(internshipUserInformationDto);
        });
        return internshipUserInformationDtos;
    }
}
