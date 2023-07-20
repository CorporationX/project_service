package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    InternshipMapper INSTANCE = Mappers.getMapper(InternshipMapper.class);

    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "interns", ignore = true)
    Internship createDtoToEntity(CreateInternshipDto dto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "internIds", source = "interns", qualifiedByName = "internsToInternIds")
    @Mapping(target = "scheduleId", source = "schedule.id")
    ResponseInternshipDto entityToResponseDto(Internship internship);

    List<ResponseInternshipDto> entityListToDtoList(List<Internship> internships);

    @Named("internsToInternIds")
    default List<Long> internsToInternIds(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
